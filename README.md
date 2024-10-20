# yasBanimento - Plugin de Banimento para Minecraft

## Visão Geral
O **yasBanimento** é um plugin para servidores BungeeCord do Minecraft que permite a moderação eficaz do servidor através do banimento de jogadores. Com um sistema de comandos intuitivo e mensagens personalizadas, este plugin ajuda a manter um ambiente seguro e agradável para todos os jogadores.

## Funcionalidades
- **Banimento por IP**: Permite banir jogadores com base em seus endereços IP.
- **Mensagens de Banimento Personalizadas**: Notificações customizadas para os jogadores banidos, informando sobre o motivo e a duração do banimento.
- **Expiração Automática de Banimentos**: Remoção automática de registros de banimentos expirados do banco de dados.
- **Suporte a Banimentos Permanentes**: Possibilidade de aplicar banimentos permanentes, com gerenciamento simplificado.

## Instalação
1. Baixe a versão mais recente do arquivo JAR do plugin yasBanimento.
2. Coloque o arquivo JAR na pasta `plugins` do seu servidor BungeeCord.
3. Inicie seu servidor para carregar o plugin.
4. Configure a conexão com o banco de dados no arquivo `Main.java`.

## Comandos
### Banir Jogador
- **Comando**: `/ban <jogador> <motivo> <tempo>`
- **Permissão**: `yasbanimento.ban`
- **Descrição**: Banir um jogador especificando o motivo e a duração (pode ser em dias, semanas, meses ou permanente).

#### Exemplos de Uso
```bash
/ban jogador123 Spam 1d    # Banir 'jogador123' por 1 dia
/ban jogador456 Insulto 1w  # Banir 'jogador456' por 1 semana
/ban jogador789 Cheating perm # Banir 'jogador789' permanentemente
