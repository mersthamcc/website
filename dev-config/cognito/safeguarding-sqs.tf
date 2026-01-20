resource "aws_sqs_queue" "safeguarding" {
  name                       = "${var.developer_name}-safeguarding"
  max_message_size           = 262144
  message_retention_seconds  = 1209600
  visibility_timeout_seconds = 900
}

resource "time_sleep" "safeguarding_wait_60_seconds" {
  depends_on = [
    aws_sqs_queue.safeguarding,
  ]
  create_duration = "30s"
}

data "aws_iam_policy_document" "safeguarding_queue_policy" {
  statement {
    sid    = "SendSQS"
    effect = "Allow"

    principals {
      type = "AWS"
      identifiers = [
        "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"
      ]
    }

    actions = [
      "sqs:SendMessage",
      "sqs:ChangeMessageVisibility",
      "sqs:GetQueueAttributes",
    ]

    resources = [
      aws_sqs_queue.safeguarding.arn
    ]
  }
}

resource "aws_sqs_queue_policy" "safeguarding_queue_policy" {
  depends_on = [
    time_sleep.safeguarding_wait_60_seconds,
  ]

  queue_url = aws_sqs_queue.safeguarding.id
  policy    = data.aws_iam_policy_document.safeguarding_queue_policy.json
}


