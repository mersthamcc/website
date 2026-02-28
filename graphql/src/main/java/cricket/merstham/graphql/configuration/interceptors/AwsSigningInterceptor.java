package cricket.merstham.graphql.configuration.interceptors;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4FamilyHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class AwsSigningInterceptor implements ClientHttpRequestInterceptor {

    private final AwsCredentialsProvider awsCredentialsProvider;
    private final String region;
    private final String service;

    public AwsSigningInterceptor(
            AwsCredentialsProvider awsCredentialsProvider, String region, String service) {
        this.awsCredentialsProvider = awsCredentialsProvider;
        this.region = region;
        this.service = service;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        SdkHttpRequest sdkHttpRequest =
                SdkHttpRequest.builder()
                        .uri(request.getURI())
                        .method(SdkHttpMethod.fromValue(request.getMethod().name()))
                        .putHeader("Content-Type", "application/json")
                        .build();

        AwsV4HttpSigner awsSigner = AwsV4HttpSigner.create();
        AwsCredentialsIdentity credentialsIdentity = awsCredentialsProvider.resolveCredentials();

        SdkHttpRequest signedRequest =
                awsSigner
                        .sign(
                                r ->
                                        r.identity(credentialsIdentity)
                                                .request(sdkHttpRequest)
                                                .payload(ContentStreamProvider.fromByteArray(body))
                                                .putProperty(
                                                        AwsV4FamilyHttpSigner.SERVICE_SIGNING_NAME,
                                                        service)
                                                .putProperty(AwsV4HttpSigner.REGION_NAME, region))
                        .request();

        HttpRequest signedHttpRequest = convertToHttpRequest(signedRequest);
        return execution.execute(signedHttpRequest, body);
    }

    private HttpRequest convertToHttpRequest(SdkHttpRequest request) {
        return new HttpRequest() {
            @Override
            public @NonNull HttpMethod getMethod() {
                return HttpMethod.valueOf(request.method().name());
            }

            @Override
            public @NonNull URI getURI() {
                return request.getUri();
            }

            @Override
            public @NonNull Map<String, Object> getAttributes() {
                return Map.of();
            }

            @Override
            public @NonNull HttpHeaders getHeaders() {
                var headers = new HttpHeaders();
                request.headers().forEach(headers::addAll);
                return headers;
            }
        };
    }
}
