# Insurance Claims Agent

An **Agentic AI** system that processes insurance claims end-to-end using LLM-powered reasoning and tool execution.

Built with **Java 21**, **Spring Boot**, **LangChain4j**, and **Ollama** (local LLM).

## Architecture

```
Customer ──► REST API ──► AI Agent ──► Tools ──► Decision
                              │
                              ├── PolicyLookupTool (look up policy details)
                              ├── CoverageCheckTool (verify damage coverage)
                              └── CostEstimatorTool (estimate repair costs)
```

### Three Agentic Patterns

| Pattern | Endpoint | Description |
|---------|----------|-------------|
| **Extraction** | `POST /api/claims/analyze` | Single LLM call to extract structured data from claim text |
| **Orchestrated Pipeline** | `POST /api/claims/process` | Multi-step agent: extract → lookup → coverage → cost → decision |
| **Conversational Agent** | `POST /api/claims/chat` | Multi-turn chat with session memory and tool access |

## Tech Stack

- **Java 21** + **Spring Boot 3.3**
- **LangChain4j** — AI agent framework (tool use, memory, structured output)
- **Ollama** + **Llama 3.1** — Local LLM (no cloud API needed)
- **Docker** + **Kubernetes** — Production deployment
- **JUnit 5** + **MockMvc** — Testing

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- [Ollama](https://ollama.ai) installed with `llama3.1` model

### Run Locally

```bash
# Pull the LLM model
ollama pull llama3.1

# Build and run
mvn clean compile
mvn spring-boot:run
```

### Run with Docker Compose

```bash
docker-compose up --build
```

## API Examples

### 1. Extract Claim Details
```bash
curl -X POST http://localhost:8080/api/claims/analyze \
  -H "Content-Type: application/json" \
  -d '{"description": "I had a car accident. My rear bumper is smashed."}'
```

### 2. Process Claim (Full Agent Pipeline)
```bash
curl -X POST http://localhost:8080/api/claims/process \
  -H "Content-Type: application/json" \
  -d '{"description": "Policy ID: POL-1001. Collision accident. Rear bumper damaged. Severity moderate."}'
```

Response:
```json
{
  "decision": "APPROVED",
  "policyId": "POL-1001",
  "damageType": "collision",
  "estimatedCost": 1400.00,
  "customerPays": 500.00,
  "insurancePays": 900.00,
  "reasoning": "Covered under collision coverage, within max limit.",
  "steps": [
    {"step": "EXTRACT", "result": "..."},
    {"step": "POLICY_LOOKUP", "result": "..."},
    {"step": "COVERAGE_CHECK", "result": "..."},
    {"step": "COST_ESTIMATE", "result": "..."}
  ]
}
```

### 3. Conversational Chat (Multi-turn)
```bash
# Turn 1
curl -X POST http://localhost:8080/api/claims/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "s1", "message": "Hi, my policy is POL-1003."}'

# Turn 2 (agent remembers context)
curl -X POST http://localhost:8080/api/claims/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "s1", "message": "A tree fell on my truck. Hood and windshield destroyed."}'
```

## Test Policies

| Policy ID | Customer | Vehicle | Plan | Status |
|-----------|----------|---------|------|--------|
| POL-1001 | John Smith | 2022 Toyota Camry | Premium | Active |
| POL-1002 | Sarah Johnson | 2020 Honda Civic | Basic | Active |
| POL-1003 | Mike Davis | 2023 Ford F-150 | Premium | Active |
| POL-1004 | Emily Brown | 2024 Tesla Model 3 | Comprehensive | Expired |

## Kubernetes Deployment

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/ollama-deployment.yaml
kubectl apply -f k8s/claims-agent-deployment.yaml
kubectl apply -f k8s/ingress.yaml
```

Features:
- **HorizontalPodAutoscaler**: 2-10 replicas based on CPU (70% threshold)
- **Health probes**: Readiness + liveness on `/api/claims/health`
- **Persistent storage**: Ollama models stored on PVC
- **ConfigMap**: Externalized configuration

## Testing

```bash
mvn test
```

19 unit tests covering:
- Tool logic (PolicyLookup, CoverageCheck, CostEstimator)
- REST API endpoints (MockMvc)
- Input validation and error handling

## Project Structure

```
src/main/java/com/geico/claims/
├── ClaimsAgentApplication.java          # Spring Boot main
├── agent/
│   ├── ClaimsAgent.java                 # LangChain4j AI Service (native tool calling)
│   ├── ClaimsAgentOrchestrator.java     # Orchestrated multi-step pipeline
│   ├── ConversationalClaimsAgent.java   # Chat agent with memory
│   ├── ClaimsAgentConfig.java           # Agent bean configuration
│   └── AgentResponse.java              # Response with step tracking
├── config/
│   └── OllamaConfig.java              # LLM configuration
├── controller/
│   └── ClaimsController.java           # REST endpoints
├── exception/
│   └── GlobalExceptionHandler.java     # Error handling
├── model/                              # Request/response records
├── service/
│   ├── ClaimExtractor.java            # AI extraction service
│   └── ClaimService.java             # Business logic
└── tools/
    ├── PolicyLookupTool.java          # @Tool - policy database
    ├── CoverageCheckTool.java         # @Tool - coverage verification
    └── CostEstimatorTool.java         # @Tool - repair cost estimation
```
