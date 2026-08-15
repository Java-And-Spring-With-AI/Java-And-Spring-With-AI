# Support Assistant

A simple Java application that analyzes customer-support messages using a locally running Ollama model.

## Prerequisites

- Java 21
- Maven
- Ollama running locally

## How to pull and start the Ollama model

1. Download and install Ollama from [ollama.com](https://ollama.com).
2. Pull the default model (llama3.2):
   ```bash
   ollama pull llama3.2
   ```
3. Ensure Ollama is running (it typically runs on `http://localhost:11434`).

## How to build and run the Java application

1. Build the project using Maven:
   ```bash
   mvn clean package
   ```
2. Run the application:
   ```bash
   java -jar target/support-assistant-1.0-SNAPSHOT.jar
   ```
   Alternatively, you can run the main class directly:
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.supportassistant.SupportAssistantApplication"
   ```

### Configuration

You can configure the application using environment variables:
- `SERVER_PORT`: Port for the application server (default: 8080).
- `OLLAMA_BASE_URL`: URL where Ollama is running (default: http://localhost:11434).
- `OLLAMA_MODEL`: The Ollama model to use (default: llama3.2).

## Request Flow

1. The user sends a `POST` request to `/api/support/requests/analyze` with a JSON body containing a support message.
2. `SupportRequestController` receives the request and parses the JSON.
3. `SupportRequestService` validates the message and prepares instructions for the AI model.
4. `OllamaModelProxy` sends a request to the local Ollama API (`/api/chat`).
5. Ollama processes the message and returns a generated response.
6. `SupportRequestService` parses the model's output into a `SupportAnalysis` object.
7. The application returns the analysis as a JSON response.

## Example Usage

### Curl Example

```bash
curl -X POST http://localhost:8080/api/support/requests/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "message": "I was charged twice for my subscription this month. I contacted support three days ago but have not received an answer."
  }'
```

### Example JSON Response

```json
{
  "summary": "The customer reports being charged twice for the same subscription.",
  "category": "BILLING",
  "urgency": "HIGH",
  "suggestedResponse": "We apologize for the duplicate charge and the delayed response. We will investigate the payments and refund any incorrect charge as quickly as possible."
}
```
