package co.com.crediya.sqs.sender.config;

import co.com.crediya.model.loantype.enums.SqsQueueType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SqsQueuesPropertiesTest {

    @Test
    void shouldReturnQueueUrlForReportsType() {

        SqsQueuesProperties props = new SqsQueuesProperties();
        SqsQueuesProperties.QueueConfig config = new SqsQueuesProperties.QueueConfig();
        config.setQueueUrl("https://sqs.aws/reports");

        props.getQueues().put("reports", config);

        String url = props.getQueueUrl(SqsQueueType.REPORTS);

        assertThat(url).isEqualTo("https://sqs.aws/reports");
    }

    @Test
    void shouldReturnQueueUrlForNotificationsType() {
        SqsQueuesProperties props = new SqsQueuesProperties();
        props.getQueues().put("notifications", new SqsQueuesProperties.QueueConfig() {{
            setQueueUrl("https://sqs.aws/notifications");
        }});

        String url = props.getQueueUrl(SqsQueueType.NOTIFICATIONS);

        assertThat(url).isEqualTo("https://sqs.aws/notifications");
    }

    @Test
    void shouldThrowExceptionIfQueueTypeNotConfigured() {
        SqsQueuesProperties props = new SqsQueuesProperties();

        assertThatThrownBy(() -> props.getQueueUrl(SqsQueueType.CAPACITY))
                .isInstanceOf(NullPointerException.class); // o IllegalStateException si blindas
    }

    @Test
    void shouldHandleMultipleQueueTypes() {
        SqsQueuesProperties props = new SqsQueuesProperties();

        props.getQueues().put("reports", new SqsQueuesProperties.QueueConfig() {{
            setQueueUrl("https://sqs.aws/reports");
        }});
        props.getQueues().put("capacity", new SqsQueuesProperties.QueueConfig() {{
            setQueueUrl("https://sqs.aws/capacity");
        }});

        assertThat(props.getQueueUrl(SqsQueueType.REPORTS)).isEqualTo("https://sqs.aws/reports");
        assertThat(props.getQueueUrl(SqsQueueType.CAPACITY)).isEqualTo("https://sqs.aws/capacity");
    }
}
