package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import cricket.merstham.graphql.repository.PaymentEntityRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class StripeWebhookProcessorTest {

    // region "Example data"
    private static String EXAMPLE_HOOK =
            "{"
                    + "  \"id\": \"evt_123456\","
                    + "  \"data\": {"
                    + "    \"object\": {"
                    + "      \"id\": \"ch_123456\","
                    + "      \"paid\": true,"
                    + "      \"order\": null,"
                    + "      \"amount\": 4000,"
                    + "      \"object\": \"charge\","
                    + "      \"review\": null,"
                    + "      \"source\": null,"
                    + "      \"status\": \"succeeded\","
                    + "      \"created\": 1706123456,"
                    + "      \"dispute\": null,"
                    + "      \"invoice\": null,"
                    + "      \"outcome\": {"
                    + "        \"type\": \"authorized\","
                    + "        \"reason\": null,"
                    + "        \"risk_level\": \"normal\","
                    + "        \"network_status\": \"approved_by_network\","
                    + "        \"seller_message\": \"Payment complete.\""
                    + "      },"
                    + "      \"refunds\": {"
                    + "        \"url\": \"/v1/charges/ch_123456/refunds\","
                    + "        \"data\": [],"
                    + "        \"object\": \"list\","
                    + "        \"has_more\": false,"
                    + "        \"total_count\": 0"
                    + "      },"
                    + "      \"captured\": true,"
                    + "      \"currency\": \"gbp\","
                    + "      \"customer\": null,"
                    + "      \"disputed\": false,"
                    + "      \"livemode\": true,"
                    + "      \"metadata\": {},"
                    + "      \"refunded\": false,"
                    + "      \"shipping\": null,"
                    + "      \"application\": null,"
                    + "      \"description\": null,"
                    + "      \"destination\": null,"
                    + "      \"receipt_url\": \"https://pay.stripe.com/receipts/payment/123456-123456\","
                    + "      \"failure_code\": null,"
                    + "      \"on_behalf_of\": null,"
                    + "      \"fraud_details\": {},"
                    + "      \"radar_options\": {},"
                    + "      \"receipt_email\": \"chris@clayson.org.uk\","
                    + "      \"transfer_data\": null,"
                    + "      \"amount_updates\": [],"
                    + "      \"payment_intent\": \"pi_123456\","
                    + "      \"payment_method\": \"pm_123456\","
                    + "      \"receipt_number\": null,"
                    + "      \"transfer_group\": null,"
                    + "      \"amount_captured\": 4000,"
                    + "      \"amount_refunded\": 0,"
                    + "      \"application_fee\": null,"
                    + "      \"billing_details\": {"
                    + "        \"name\": \"Chris Clayson\","
                    + "        \"email\": \"chris@clayson.org.uk\","
                    + "        \"phone\": null,"
                    + "        \"address\": {"
                    + "          \"city\": \"Horley\","
                    + "          \"line1\": \"6 Heronswood Court\","
                    + "          \"line2\": \"\","
                    + "          \"state\": \"England\","
                    + "          \"country\": \"GB\","
                    + "          \"postal_code\": \"RH6 9PW\""
                    + "        }"
                    + "      },"
                    + "      \"failure_message\": null,"
                    + "      \"source_transfer\": null,"
                    + "      \"balance_transaction\": \"txn_123456\","
                    + "      \"statement_descriptor\": null,"
                    + "      \"application_fee_amount\": null,"
                    + "      \"payment_method_details\": {"
                    + "        \"card\": {"
                    + "          \"brand\": \"mastercard\","
                    + "          \"last4\": \"3290\","
                    + "          \"checks\": {"
                    + "            \"cvc_check\": null,"
                    + "            \"address_line1_check\": \"pass\","
                    + "            \"address_postal_code_check\": \"pass\""
                    + "          },"
                    + "          \"wallet\": {"
                    + "            \"type\": \"apple_pay\","
                    + "            \"apple_pay\": {"
                    + "              \"type\": \"apple_pay\""
                    + "            },"
                    + "            \"dynamic_last4\": \"1234\""
                    + "          },"
                    + "          \"country\": \"GB\","
                    + "          \"funding\": \"debit\","
                    + "          \"mandate\": null,"
                    + "          \"network\": \"mastercard\","
                    + "          \"exp_year\": 2027,"
                    + "          \"exp_month\": 2,"
                    + "          \"fingerprint\": \"123456\","
                    + "          \"overcapture\": {"
                    + "            \"status\": \"unavailable\","
                    + "            \"maximum_amount_capturable\": 4000"
                    + "          },"
                    + "          \"installments\": null,"
                    + "          \"multicapture\": {"
                    + "            \"status\": \"unavailable\""
                    + "          },"
                    + "          \"network_token\": {"
                    + "            \"used\": false"
                    + "          },"
                    + "          \"three_d_secure\": null,"
                    + "          \"amount_authorized\": 4000,"
                    + "          \"extended_authorization\": {"
                    + "            \"status\": \"disabled\""
                    + "          },"
                    + "          \"incremental_authorization\": {"
                    + "            \"status\": \"unavailable\""
                    + "          }"
                    + "        },"
                    + "        \"type\": \"card\""
                    + "      },"
                    + "      \"failure_balance_transaction\": null,"
                    + "      \"statement_descriptor_suffix\": null,"
                    + "      \"calculated_statement_descriptor\": \"CRICKET CLUB\""
                    + "    }"
                    + "  },"
                    + "  \"type\": \"charge.succeeded\","
                    + "  \"object\": \"event\","
                    + "  \"created\": 1706123456,"
                    + "  \"request\": \"req_123456\","
                    + "  \"livemode\": true,"
                    + "  \"api_version\": \"2017-02-14\","
                    + "  \"pending_webhooks\": 0"
                    + "}";
    // endregion

    private final PaymentEntityRepository repository = mock(PaymentEntityRepository.class);
    private final StripeWebhookProcessor processor = new StripeWebhookProcessor("", "", repository);
    private final ObjectMapper objectMapper = new JsonMapper();

    @Test
    void getName() {
        assertThat(processor.getName()).isEqualTo("stripe");
    }

    @Test
    void getPaymentReference() throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(EXAMPLE_HOOK);
        var result = processor.getPaymentReference(node);

        assertThat(result).isEqualTo("pi_123456");
    }
}
