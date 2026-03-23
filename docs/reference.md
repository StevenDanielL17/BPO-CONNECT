BPO Service Connect
Software Engineering Documentation
Exercises 1 – 8
Inbound Customer Support Module

[Note: This is the full reference documentation provided by the user]

Ex. No: 1 Identification of Software System in BPO Management
Aim: To identify and analyze a software system for managing non-core business processes of Customer Support in a BPO environment.

Problem Context:
A BPO company handles inbound customer support...

(The system includes Users, Customers, Tickets, SLAs, CallRecords, QualityEvaluations, Feedback, SupportReports, and Services like ScreenPopController, TicketService, NotificationService.)

Ex. No: 2 Software Requirements Specification (SRS)
Provides functional (FR-01 to FR-19) and non-functional requirements.

Ex. No: 3 Use Case Identification and Use Case Model
Identifies actors: Support Agent, Customer, Team Leader, Quality Analyst, System Admin, Telephony System, Notification Service.

Ex. No: 4 Domain Modelling and Class Diagram
Classes: User, SupportAgent, TeamLeader, QualityAnalyst, SystemAdmin, Customer, Ticket, SLA, KnowledgeBaseArticle, CallRecord, Queue, QualityEvaluation, Feedback, SupportReport, ScreenPopController, TicketService, NotificationService.

Ex. No: 8 Design Patterns
1. Observer: TicketService -> NotificationService
2. Factory Method: TicketService.createTicket()
3. Strategy: SLA.triggerEscalation()
4. Singleton: ScreenPopController, NotificationService
5. Template Method: SupportReport.generate()
