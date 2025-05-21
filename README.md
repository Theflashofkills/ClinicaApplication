## Padrões de Projeto Implementados

### SOLID

- **Single Responsibility Principle (SRP):** Cada classe possui responsabilidade única, facilitando manutenção e testes.  
- **Open/Closed Principle (OCP):** O sistema permite extensão, como a estratégia de envio de mensagens no chatbot, sem modificar código existente.  
- **Liskov Substitution Principle (LSP):** As estratégias implementam interfaces que garantem substituibilidade sem alterar o funcionamento.  
- **Interface Segregation Principle (ISP):** Interfaces específicas evitam dependências desnecessárias.  
- **Dependency Inversion Principle (DIP):** Controladores dependem de abstrações e não de implementações concretas.

### Strategy

- Aplicado no módulo de ChatBot para permitir múltiplas estratégias de envio e processamento de mensagens, facilitando futuras integrações com outras APIs.

## Implementações Realizadas Conforme Planejamento do Jira

- Login e cadastro via FirebaseAuth  
- Cadastro e listagem de exames integrados com Firebase Firestore e Couchbase Lite  
- Upload de imagens de perfil com Firebase Storage  
- ChatBot integrado com API OpenAI utilizando estratégia para envio de mensagens  
- Fluxo completo de navegação e logout  
- Exclusão de conta com remoção de dados nos serviços Firebase

