#!/bin/bash
echo "Inicializando LocalStack..."

# Criar Tópico SNS
awslocal sns create-topic --name order-payments

# Criar Fila SQS
awslocal sqs create-queue --queue-name payment-status-queue

# (Opcional) Inscrever a fila no tópico se necessário para testes de fluxo completo
# awslocal sns subscribe \
#   --topic-arn arn:aws:sns:us-east-1:000000000000:order-payments \
#   --protocol sqs \
#   --notification-endpoint arn:aws:sqs:us-east-1:000000000000:payment-status-queue

echo "LocalStack inicializado com sucesso!"