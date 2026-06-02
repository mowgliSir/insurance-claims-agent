# Agentic AI — A Beginner's Guide (Plain English)

---

## 1. What is an Agent? (The Simplest Explanation)

Imagine you go to a restaurant:

```
WITHOUT Agent (Regular Microservice):
    You → Press button on kiosk → "Burger Combo #3" → You get exactly Combo #3
    The kiosk doesn't THINK. It just follows rules.

WITH Agent (Agentic AI):
    You → Talk to a waiter → "I'm hungry, something spicy but not too heavy"
    The waiter THINKS → checks menu → asks "do you eat chicken?" →
    suggests a dish → takes your order
    The waiter UNDERSTANDS, THINKS, and DECIDES.
```

**An Agent = An AI that can THINK + USE TOOLS + MAKE DECISIONS**

That's it. Nothing more complicated than that.

---

## 2. Regular Code vs Agent — What Changes?

### Regular Java Code (No AI)
```
You write every rule yourself:

    if (description.contains("bumper")) {
        damageType = "collision";
    } else if (description.contains("stolen")) {
        damageType = "theft";
    }
    // What about "a deer smashed into my car on I-95"?
    // Your if-else FAILS. You can't predict every sentence.
```

### Agent Code (With AI)
```
You ask the LLM to understand:

    model.generate("What type of damage is this: 'a deer smashed into my car on I-95'")
    → LLM returns: "collision"

    model.generate("What type of damage is this: 'someone stole my GPS from my car'")
    → LLM returns: "theft"

    It understands ANY sentence. No if-else needed.
```

---

## 3. What are "Tools"? (The Key Concept)

Think of it like this:

```
An Agent is like a SMART EMPLOYEE.
Tools are like the RESOURCES the employee has access to.

Employee (Agent) has:
    📋 Tool 1: Customer Database  → look up customer info
    📊 Tool 2: Price Calculator   → calculate repair costs
    ✅ Tool 3: Coverage Checker   → check what's covered

The employee DECIDES which tool to use and when.
You don't tell them "first use tool 1, then tool 2."
They figure it out based on the situation.
```

In Java code, a Tool is just a regular method with `@Tool` annotation:

```java
@Tool("Look up a policy by ID")
public String lookupPolicy(String policyId) {
    return database.findPolicy(policyId);  // regular Java code!
}
```

The AI reads the description "Look up a policy by ID" and knows
WHEN to call this method. You don't write if-else for this.

---

## 4. How Does It Work Under the Hood? (Step by Step)

Let's trace what happens when a user sends this request:

```
POST /api/claims/process
{
    "description": "Policy POL-1001. Truck hit my car. Rear bumper smashed. Severe."
}
```

### STEP-BY-STEP FLOW:

```
┌─────────────────────────────────────────────────────┐
│  STEP 1: User sends request                         │
│                                                     │
│  "Policy POL-1001. Truck hit my car.                │
│   Rear bumper smashed. Severe."                     │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│  STEP 2: Spring Boot receives it (normal stuff)     │
│                                                     │
│  ClaimsController.processClaim(request)              │
│  → calls ClaimsAgentOrchestrator.processClaim()      │
│                                                     │
│  This part is SAME as any microservice.              │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│  STEP 3: LLM THINKS (this is the AGENT part)        │
│                                                     │
│  Our code sends to Ollama (local AI):                │
│  "Extract damage info from this claim..."            │
│                                                     │
│  Ollama (LLM) READS the text and UNDERSTANDS:       │
│    → Policy ID: POL-1001                             │
│    → Damage Type: collision                          │
│    → Damaged Part: rear bumper                       │
│    → Severity: severe                                │
│                                                     │
│  No if-else! The AI figured it out from plain text.  │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│  STEP 4: TOOL CALLS (Agent uses its tools)           │
│                                                     │
│  Tool 1 - PolicyLookupTool:                          │
│    Input:  "POL-1001"                                │
│    Output: "John Smith, Toyota Camry, Premium plan,  │
│             collision coverage, $500 deductible"     │
│                                                     │
│  Tool 2 - CoverageCheckTool:                         │
│    Input:  damage="collision", coverages="collision,  │
│            comprehensive, liability, theft"           │
│    Output: "COVERED"                                 │
│                                                     │
│  Tool 3 - CostEstimatorTool:                         │
│    Input:  part="rear bumper", severity="severe",     │
│            deductible=500                            │
│    Output: "Total: $2520, Customer: $500,             │
│             Insurance: $2020"                        │
│                                                     │
│  These are regular Java methods! Nothing fancy.       │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│  STEP 5: LLM DECIDES (Agent makes the final call)   │
│                                                     │
│  Our code sends ALL the collected info to Ollama:    │
│  "Here's the policy, coverage, and cost.             │
│   Should we APPROVE, DENY, or ESCALATE?"             │
│                                                     │
│  Ollama REASONS:                                     │
│    "Policy is active ✓                               │
│     Damage is covered ✓                              │
│     Cost $2520 is within $50,000 max ✓               │
│     → DECISION: APPROVED"                            │
│                                                     │
│  The AI made a JUDGMENT. Not a hardcoded if-else.    │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│  STEP 6: Response sent back (normal stuff)           │
│                                                     │
│  {                                                   │
│    "decision": "APPROVED",                           │
│    "estimatedCost": 2520.00,                         │
│    "customerPays": 500.00,                           │
│    "insurancePays": 2020.00,                         │
│    "reasoning": "Covered under collision..."         │
│  }                                                   │
└─────────────────────────────────────────────────────┘
```

---

## 5. Where is the "Magic"? What's Different From Microservices?

```
REGULAR MICROSERVICE:                    AGENTIC AI:

User Input                               User Input
   │                                        │
   ▼                                        ▼
[Your Code]                              [Your Code]
   │                                        │
   ▼                                        ▼
if/else/switch ← YOU decide logic     ┌──[LLM BRAIN]──┐ ← AI decides
   │                                  │  Understands   │
   ▼                                  │  Thinks        │
Call Service A                        │  Decides       │
   │                                  └───────┬────────┘
   ▼                                          │
Call Service B                          Which tool to use?
   │                                    ┌─────┼─────┐
   ▼                                    ▼     ▼     ▼
Return Response                      Tool A Tool B Tool C
                                        │     │     │
                                        └─────┼─────┘
                                              ▼
                                        [LLM BRAIN]
                                        Makes decision
                                              │
                                              ▼
                                        Return Response
```

### The 3 Differences:

```
╔══════════════════╦═══════════════════════╦═══════════════════════╗
║                  ║  MICROSERVICE         ║  AGENT                ║
╠══════════════════╬═══════════════════════╬═══════════════════════╣
║ Understanding    ║  Structured JSON      ║  Natural language     ║
║ Input            ║  {damageType:"theft"} ║  "someone stole my    ║
║                  ║                       ║   GPS last night"     ║
╠══════════════════╬═══════════════════════╬═══════════════════════╣
║ Logic            ║  if/else hardcoded    ║  LLM reasons and      ║
║                  ║  by developer         ║  thinks like a human  ║
╠══════════════════╬═══════════════════════╬═══════════════════════╣
║ Decision Making  ║  Rule-based           ║  AI judgment based    ║
║                  ║  (cost < max → yes)   ║  on all context       ║
╚══════════════════╩═══════════════════════╩═══════════════════════╝
```

---

## 6. How to Write an Agent (3 Simple Steps)

### Step 1: Create Tools (Regular Java Methods)

```java
// This is just a normal Java method!
// The @Tool annotation tells the AI what this method does.

@Tool("Look up customer policy by ID")
public String lookupPolicy(String policyId) {
    // Normal database call, API call, anything
    return policyRepository.findById(policyId);
}

@Tool("Check if damage is covered by insurance")
public String checkCoverage(String damageType, String coverages) {
    // Normal business logic
    if (coverages.contains(damageType)) return "COVERED";
    return "NOT COVERED";
}
```

### Step 2: Create the Agent Interface

```java
// This is where the "magic" is.
// You tell the AI WHO it is and WHAT it should do.

public interface ClaimsAgent {

    @SystemMessage("""
        You are an insurance claims agent.
        When a customer describes a claim:
        1. Look up their policy
        2. Check if damage is covered
        3. Estimate the cost
        4. Approve or deny
        """)
    String processClaim(String claimDescription);
}
```

### Step 3: Wire It Together

```java
// Connect the AI model + tools + agent

ClaimsAgent agent = AiServices.builder(ClaimsAgent.class)
    .chatLanguageModel(ollamaModel)       // the AI brain
    .tools(policyTool, coverageTool)      // the tools it can use
    .chatMemory(memory)                   // optional: remember conversations
    .build();

// Now just call it!
String result = agent.processClaim("My car was hit by a truck...");
```

**That's it!** Three files. The AI does the rest.

---

## 7. Do I Need a UI? How Does It Connect?

### Short Answer: The agent is a BACKEND service. UI is separate.

```
┌──────────────────┐     HTTP/REST      ┌──────────────────┐
│                  │ ──────────────────► │                  │
│   ANY UI         │                    │   Your Agent     │
│                  │ ◄────────────────── │   (Spring Boot)  │
│  - React App     │     JSON Response  │                  │
│  - Mobile App    │                    │  POST /api/chat  │
│  - Chatbot       │                    │  POST /api/process│
│  - Postman       │                    │                  │
│  - Slack Bot     │                    │                  │
│  - WhatsApp      │                    │                  │
└──────────────────┘                    └──────────────────┘
```

### The UI just sends HTTP requests (like any other API):

```
REACT EXAMPLE:

const response = await fetch('/api/claims/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
        sessionId: 'user-123',
        message: 'My car was in an accident, policy POL-1001'
    })
});

const data = await response.json();
// data.reply = "I found your policy, John. Can you describe the damage?"
```

**The UI doesn't know it's talking to an agent.** It's just a REST API.
The "intelligence" is all in the backend.

---

## 8. How Does This Fit in a Real GEICO-Like Project?

### The Big Picture:

```
                        ┌─────────────────────────────┐
                        │      GEICO Website/App       │
                        │    (React / Mobile App)      │
                        └──────────────┬──────────────┘
                                       │
                                       ▼
                        ┌─────────────────────────────┐
                        │       API Gateway            │
                        │   (Authentication, Routing)  │
                        └──────────────┬──────────────┘
                                       │
                 ┌─────────────────────┼─────────────────────┐
                 │                     │                     │
                 ▼                     ▼                     ▼
        ┌────────────────┐  ┌──────────────────┐  ┌────────────────┐
        │ Claims Agent   │  │ Customer Service │  │ Underwriting   │
        │ (OUR PROJECT)  │  │ Agent            │  │ Agent          │
        │                │  │                  │  │                │
        │ Processes      │  │ Answers questions│  │ Evaluates risk │
        │ insurance      │  │ about policies,  │  │ for new        │
        │ claims         │  │ billing, etc.    │  │ policies       │
        └───────┬────────┘  └────────┬─────────┘  └───────┬────────┘
                │                    │                     │
                ▼                    ▼                     ▼
        ┌─────────────────────────────────────────────────────────┐
        │                   SHARED SERVICES                       │
        │                                                         │
        │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────┐ │
        │  │ Policy   │  │ Customer │  │ Payment  │  │ LLM    │ │
        │  │ Database │  │ Database │  │ Service  │  │ Service│ │
        │  └──────────┘  └──────────┘  └──────────┘  └────────┘ │
        └─────────────────────────────────────────────────────────┘
```

### Where Agents Fit in Microservices:

```
BEFORE (Traditional):
    User → API → Microservice A → Microservice B → Database → Response
    Everything is hardcoded, rule-based.

AFTER (With Agents):
    User → API → Agent → (calls Microservice A, B, C as needed) → Response
    Agent DECIDES which services to call based on the situation.

The agent REPLACES the hardcoded orchestration layer.
The microservices (databases, APIs) STAY THE SAME.
```

---

## 9. What is the Conversation Memory? (Why is it Important?)

### Without Memory (Stateless — like regular API):

```
User: "My policy is POL-1001"
Agent: "Found your policy, John Smith!"

User: "What's my deductible?"
Agent: "I don't know who you are. What's your policy ID?"  ← FORGOT!
```

### With Memory (Stateful — like a real conversation):

```
User: "My policy is POL-1001"
Agent: "Found your policy, John Smith!"
        [Memory saves: sessionId=123, policy=POL-1001, name=John]

User: "What's my deductible?"
Agent: "Your deductible is $500, John."  ← REMEMBERS!
        [Memory still has the context from before]
```

### How Memory Works in Code:

```java
// Each session gets its own memory (like each customer has their own notebook)
ChatMemoryProvider memoryProvider = sessionId ->
    MessageWindowChatMemory.withMaxMessages(20);  // remember last 20 messages

// The agent automatically uses it
agent.chat("session-123", "My policy is POL-1001");   // stored in session-123
agent.chat("session-123", "What's my deductible?");    // reads from session-123
agent.chat("session-456", "Hi");                       // different session, no memory of 123
```

---

## 10. How Ollama/LLM Fits In (The AI Brain)

```
YOUR APP                          OLLAMA (running locally)
┌──────────────┐                  ┌──────────────────────┐
│              │   HTTP call      │                      │
│  Java Code   │ ───────────────► │  Llama 3.1 Model     │
│              │  "understand     │  (AI Brain)          │
│  LangChain4j │   this claim"    │                      │
│              │                  │  Reads your text     │
│              │ ◄─────────────── │  Thinks about it     │
│              │  returns answer  │  Returns answer      │
└──────────────┘                  └──────────────────────┘

Ollama is like a LOCAL ChatGPT running on your machine.
Your Java code sends questions to it and gets answers.
LangChain4j is the LIBRARY that makes this easy in Java.
```

### In Production (Real GEICO):

```
Instead of Ollama (local), you would use:
    - Azure OpenAI (GPT-4)        ← Microsoft cloud
    - AWS Bedrock (Claude)         ← Amazon cloud
    - Google Vertex AI (Gemini)    ← Google cloud

The code stays ALMOST the same. You just change the config:

    LOCAL:      OllamaChatModel.builder().baseUrl("http://localhost:11434")
    PRODUCTION: AzureOpenAiChatModel.builder().endpoint("https://geico.openai.azure.com")

Everything else (tools, agent, memory) stays identical.
```

---

## 11. Complete Flow — From User Click to Response

```
 CUSTOMER
 clicks "Submit Claim"
 on GEICO website
      │
      │  1. Browser sends HTTP POST
      ▼
 ┌─────────────┐
 │   React UI  │  POST /api/claims/chat
 │   (Website) │  { sessionId: "abc", message: "I had a
 └──────┬──────┘    car accident..." }
        │
        │  2. Request hits Spring Boot
        ▼
 ┌─────────────┐
 │  Controller │  ClaimsController.chat(request)
 └──────┬──────┘
        │
        │  3. Agent receives the message
        ▼
 ┌─────────────────────────────────────────────────────┐
 │                   AI AGENT                          │
 │                                                     │
 │  a. Loads chat memory for this session              │
 │     (remembers previous messages)                   │
 │                                                     │
 │  b. Sends message + memory + tool descriptions      │
 │     to LLM (Ollama)                                 │
 │                                                     │
 │  c. LLM THINKS:                                     │
 │     "Customer had an accident. I need to:            │
 │      - find their policy                            │
 │      - check coverage                              │
 │      - estimate cost"                               │
 │                                                     │
 │  d. LLM says: "Call lookupPolicy(POL-1001)"         │
 │     → Java code executes the tool                   │
 │     → Result sent back to LLM                       │
 │                                                     │
 │  e. LLM says: "Call checkCoverage(collision, ...)"  │
 │     → Java code executes the tool                   │
 │     → Result sent back to LLM                       │
 │                                                     │
 │  f. LLM says: "Call estimateCost(bumper, severe)"   │
 │     → Java code executes the tool                   │
 │     → Result sent back to LLM                       │
 │                                                     │
 │  g. LLM DECIDES: "Everything checks out. APPROVED." │
 │     → Returns final answer                          │
 │                                                     │
 │  h. Save this conversation to memory                │
 └──────────────────────┬──────────────────────────────┘
                        │
                        │  4. Response sent back
                        ▼
                 ┌─────────────┐
                 │   React UI  │  Shows: "Your claim has
                 │   (Website) │  been APPROVED. Estimated
                 └─────────────┘  cost: $2520..."
```

---

## 12. Summary — The 5 Things to Remember

```
1. AGENT = AI that can THINK + USE TOOLS + MAKE DECISIONS
   (not just a chatbot that answers questions)

2. TOOLS = Regular Java methods that the AI can call
   (database lookups, API calls, calculations — anything)

3. MEMORY = Agent remembers the conversation
   (like a human employee remembers what you said 2 minutes ago)

4. LLM (Ollama) = The "brain" that understands, thinks, decides
   (your code sends questions, LLM returns answers)

5. IT'S STILL A MICROSERVICE underneath!
   (Spring Boot, REST API, Docker, Kubernetes — all the same)
   The only NEW thing is: LLM replaces your if-else logic.
```

### One Line Summary:

> **Agentic AI = A microservice where an LLM replaces your hardcoded
> business logic with intelligent reasoning.**

---

## 13. What You Built (Your Project Map)

```
insurance-claims-agent/
│
├── controller/ClaimsController.java    ← SAME as any microservice (REST API)
│
├── tools/                              ← Regular Java methods
│   ├── PolicyLookupTool.java           ← @Tool: database lookup
│   ├── CoverageCheckTool.java          ← @Tool: business rule
│   └── CostEstimatorTool.java          ← @Tool: calculation
│
├── agent/                              ← THE NEW PART (Agentic AI)
│   ├── ClaimsAgent.java                ← Agent interface (AI decides tool order)
│   ├── ClaimsAgentOrchestrator.java    ← Orchestrated agent (code controls flow)
│   └── ConversationalClaimsAgent.java  ← Chat agent (with memory)
│
├── config/OllamaConfig.java            ← LLM connection config
├── model/                              ← Request/response objects (same as any API)
├── Dockerfile                          ← SAME as any microservice
└── k8s/                                ← SAME as any microservice
```

### What's SAME as regular microservices:
- Spring Boot, REST controllers, Docker, Kubernetes, testing, Maven

### What's NEW (Agentic AI):
- LangChain4j library
- @Tool annotated methods
- AI Service interfaces with @SystemMessage
- Chat memory
- LLM connection (Ollama/OpenAI/etc.)

---

*Created as part of the GEICO Sr Staff Engineer interview preparation project.*
*GitHub: https://github.com/mowgliSir/insurance-claims-agent*
