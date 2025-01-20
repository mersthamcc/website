resource "aws_sqs_queue" "transactions" {
  name                       = "${var.developer_name}-transactions.fifo"
  max_message_size           = 262144
  message_retention_seconds  = 1209600
  visibility_timeout_seconds = 900

  fifo_queue                  = true
  content_based_deduplication = false
}

resource "aws_sqs_queue" "transaction-responses" {
  name                       = "${var.developer_name}-transaction-responses"
  max_message_size           = 262144
  message_retention_seconds  = 1209600
  visibility_timeout_seconds = 900

  fifo_queue                  = false
  content_based_deduplication = false
}

resource "time_sleep" "wait_60_seconds" {
  depends_on = [
    aws_sqs_queue.transactions,
    aws_sqs_queue.transaction-responses,
  ]
  create_duration = "60s"
}

data "aws_iam_policy_document" "process_transactions_queue_policy_document" {
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
      aws_sqs_queue.transactions.arn
    ]
  }

  depends_on = [
    time_sleep.wait_60_seconds,
  ]
}

data "aws_iam_policy_document" "process_transactions_response_queue_policy_document" {
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
      aws_sqs_queue.transaction-responses.arn
    ]
  }

  statement {
    sid    = "ReceiveSQS"
    effect = "Allow"

    principals {
      type = "AWS"
      identifiers = [
        "arn:aws:iam::${data.aws_caller_identity.current.account_id}:root"
      ]
    }

    actions = [
      "sqs:ReceiveMessage",
      "sqs:DeleteMessage",
      "sqs:GetQueueAttributes",
    ]

    resources = [
      aws_sqs_queue.transaction-responses.arn
    ]
  }

  depends_on = [
    time_sleep.wait_60_seconds,
  ]
}

resource "aws_sqs_queue_policy" "process_transactions_queue_policy" {
  depends_on = [
    time_sleep.wait_60_seconds,
    data.aws_iam_policy_document.process_transactions_queue_policy_document,
  ]

  queue_url = aws_sqs_queue.transactions.id
  policy    = data.aws_iam_policy_document.process_transactions_queue_policy_document.json
}

resource "aws_sqs_queue_policy" "process_transaction_response_queue_policy" {
  depends_on = [
    time_sleep.wait_60_seconds,
    data.aws_iam_policy_document.process_transactions_response_queue_policy_document,
  ]

  queue_url = aws_sqs_queue.transaction-responses.id
  policy    = data.aws_iam_policy_document.process_transactions_response_queue_policy_document.json
}
