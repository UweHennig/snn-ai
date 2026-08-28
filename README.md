# Projekt AI Spiking Neuronal Network (SNN)

With this development, I would like to show that it is possible to develop an asynchronous, recurrent, artificial intelligence SNN with Java. <br>The reinforcement learning agent connects the autonomously operating neural network with the asynchronously running environment.

## **Core Architectural Principles**

### Neuronal Representation
- Every neuronal element (receptor, dendrite, soma, axon, synapse, effector, feedback) is represented explicitly.
- Each element has a **single, well-defined responsibility**.
- Biological terminology is used wherever possible to maintain conceptual clarity.

---

### Continuous Learning Coupling
- The SNN is **never decoupled** from the learning process.
- Learning signals originate **exclusively from the environment**.
- No supervisory system interferes with the learning dynamics.
- All data flowing through the system consists of **delta values**, not absolute states.

---

### Self-Regulation
- The network must regulate itself through:
    - spike dynamics  
    - synaptic plasticity  
    - STDP / LTP
    - threshold 
    - potential
    - weight
    - refraction time  
    - homeostasis  
    - neurogenesis (planned future component)

---

### Asynchronous Communication
- All communication is **fully asynchronous**.
- No global clock, no synchronous update cycles.
- Java virtual threads are used to maximize concurrency.

---

### Agent as Mediator
- The agent is not a controller.
- It is a **translator** between environment signals and neuronal stimuli.
- It does not impose behavior or policies.

---

### Environment as Learning Unit
- The productive environment is the **actual learning system**.
- It provides:
    - raw sensory data  
    - reward signals  
    - correction signals  
    - contextual feedback  

---

### No Higher-Level Interference
- No external logic manipulates neuron states.
- No “AI layer” sits above the SNN.
- The SNN must evolve through its own dynamics.

---

### Performance & Scalability
- Maximum performance is a core requirement.
- All data is stored Off-Heap using **Java Arena**.
- No additional tools or frameworks unless absolutely necessary.
- GPU integration is currently considered unnecessary and not feasible.

---

## **Event & Processing Architecture**

### Batch-Driven Processing
- All runtime data is processed as **matrix batches**.
- A worker processes a batch **fully**, ensuring deterministic behavior.

### Axon as Processing Boundary
- A worker runs the pipeline only until the **axon**.
- The axon generates a **new synapse matrix**.
- The current event ends; the new matrix becomes a new input event.

### Two-Queue System
- **Input Queue**: large batch events  
- **Processing Queue**: neuron-level events  
- Prevents event storms and starvation.

### Transfer Threads
- Transfer threads only move events **Input → Processing**.
- Their number is **dynamic**, enabling load balancing.

### Worker Scheduling
- Workers process **full matrix batches**, not micro-events.
- This avoids CPU thread stickiness and ensures real parallelism.

### Controlled Fan‑Out
- Axon fan-out is bundled into a **single synapse matrix**, not thousands of micro-events.

---

#### Project status
28.8.2026 : In progress with new design
13.6.2026 : In progress
<br>30.5.2026 : Project created

