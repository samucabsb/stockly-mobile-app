# Revisão Interna da Entrega

## Resultado

Projeto revisado e reorganizado para entrega acadêmica/portfólio.

## Correções aplicadas

- O código original estava compactado em uma linha em vários arquivos; todos os arquivos principais foram reescritos com formatação legível.
- A lógica de perfis foi removida do fluxo funcional.
- A classe `Perm` foi removida.
- A sessão não armazena mais `role`.
- O banco não cria mais coluna `role`.
- A criação de usuário não define mais `ADMIN`, `EMPLOYEE` ou `VIEWER`.
- A dashboard sempre mostra todas as ações.
- A exclusão de produtos está disponível para qualquer usuário logado.
- A tela de usuários controla apenas status ativo/inativo.
- Foram adicionados dados iniciais para teste.

## Pontos revisados

- Imports Java.
- Manifest.
- Build Gradle.
- Nomes de tabelas e colunas.
- Fluxo de login/logout.
- Feedbacks por Toast.
- Confirmações para ações destrutivas.
- Estados vazios em listas.
- README e instruções de execução.

## Observação

O wrapper Gradle completo normalmente inclui `gradle-wrapper.jar`. Como esta entrega foi gerada em ambiente isolado, recomenda-se abrir pelo Android Studio, que sincroniza o projeto automaticamente, ou executar com Gradle instalado localmente.
