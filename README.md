# Stockly — App Mobile Android sem Perfis

Projeto Android nativo em Java para controle de estoque, preparado para apresentação acadêmica, portfólio e entrevista.

## O que foi corrigido nesta versão

- Removida a divisão de funcionalidades por perfil de usuário.
- Removidos os papéis `ADMIN`, `EMPLOYEE` e `VIEWER` do fluxo da aplicação.
- Todos os usuários autenticados possuem acesso completo às funcionalidades.
- Código reformatado em múltiplas linhas, com nomes claros e responsabilidades separadas.
- Interface revisada com estados vazios, confirmações, feedbacks por Toast e botões consistentes.
- Banco SQLite recriado em nova versão, sem coluna `role`.
- Dados iniciais úteis para teste e demonstração.

## Tecnologias

- Java
- Android SDK nativo
- SQLiteOpenHelper
- SharedPreferences
- Gradle Kotlin DSL
- Sem AndroidX e sem bibliotecas externas

## Arquitetura

```text
app/src/main/java/br/com/samuel/stockly/
├── LoginActivity.java          # Login e cadastro
├── DashboardActivity.java      # Dashboard e lista de produtos
├── ProductFormActivity.java    # Cadastro e edição de produtos
├── UsersActivity.java          # Listagem e status dos usuários
├── SqlActivity.java            # Visualização de tabelas SQLite
├── LogActivity.java            # Movimentações e auditoria
├── AuthRepository.java         # Regras de autenticação
├── ProductRepository.java      # Regras de produto
├── DB.java                     # Banco, schema, seeds e queries
├── Session.java                # Sessão local
├── Product.java                # Modelo de produto
├── UserAccount.java            # Modelo de usuário
├── Entry.java                  # Modelo de item de log
└── Ui.java                     # Componentes visuais reutilizáveis
```

## Usuário de teste

```text
E-mail: demo@stockly.com
Senha: 1234
```

## Como executar

1. Extraia o ZIP.
2. Abra a pasta no Android Studio ou VS Code com suporte Android/Gradle.
3. Aguarde a sincronização do Gradle.
4. Execute em um emulador ou dispositivo físico Android.

## Scripts úteis

Recomendado: abrir e executar pelo Android Studio.

Se você tiver Gradle instalado localmente, também pode rodar:

```bash
gradle assembleDebug
gradle clean
```

Os arquivos `gradlew` e `gradlew.bat` foram mantidos como facilitadores para chamar o Gradle local. O arquivo binário `gradle-wrapper.jar` não foi incluído nesta entrega gerada em ambiente isolado.

## Banco de dados

Banco local:

```text
stockly_sem_perfis.db
```

Tabelas:

- `users`
- `products`
- `stock_movements`
- `audit_logs`

Observação: a versão do banco foi atualizada para recriar o schema e remover definitivamente a coluna `role`.

## Funcionalidades

- Login e cadastro de usuário
- Sessão persistente
- Cadastro, edição, busca e exclusão de produtos
- Cálculo de totais, unidades e produtos críticos
- Registro de movimentações de estoque
- Auditoria de ações relevantes
- Visualização das tabelas SQLite dentro do app
- Controle de status de usuários: ativo/inativo

## Observações de segurança

- As senhas são salvas com hash SHA-256 e salt individual.
- O projeto é educacional/offline e não possui backend remoto.
- Como todos os usuários têm acesso completo, não existe camada de autorização por perfil.

## Checklist de revisão aplicado

- Código Java reformatado e legível.
- Remoção de permissões por perfil.
- Remoção da coluna `role` do banco.
- Remoção da classe `Perm`.
- Ajuste do fluxo de login/cadastro.
- Ajuste da dashboard para sempre exibir todas as funções.
- Ajuste da tela de usuários para não alternar perfil.
- Feedbacks visuais em ações importantes.
- README atualizado com instalação, execução e usuário de teste.

## Limitações conhecidas

Este projeto foi mantido propositalmente simples e offline. Para produção real, seria recomendável adicionar backend, autenticação com tokens, criptografia reforçada, testes automatizados e controle de permissões no servidor.
