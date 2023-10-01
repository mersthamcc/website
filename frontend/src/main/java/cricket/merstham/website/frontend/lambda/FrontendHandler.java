package cricket.merstham.website.frontend.lambda;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandlerBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import cricket.merstham.website.frontend.FrontendApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FrontendHandler implements RequestStreamHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse>
            HANDLER;

    static {
        try {
            LambdaContainerHandler.getContainerConfig().setInitializationTimeout(29_000);
            HANDLER =
                    new SpringBootProxyHandlerBuilder<AwsProxyRequest>()
                            .defaultProxy()
                            .asyncInit()
                            .springBootApplication(FrontendApplication.class)
                            .buildAndInitialize();
        } catch (ContainerInitializationException e) {
            LOGGER.error("Something went wrong while initializing Spring Boot Application", e);
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context)
            throws IOException {
        HANDLER.proxyStream(input, output, context);
    }
}
