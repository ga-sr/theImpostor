# Impostor

Jogo de festa para jogar com um celular só. Todo mundo recebe a mesma palavra secreta, menos o impostor. Cada um fala uma palavra ligada ao tema, e o grupo tenta descobrir quem está blefando.

- Jogue no navegador: https://ga-sr.github.io/theImpostor/
- Baixe o app Android: [última versão](https://github.com/ga-sr/theImpostor/releases/latest)

## Como jogar

1. Adicione de 3 a 20 jogadores. Se você deixar o nome vazio, o jogo cria "Jogador N".
2. Ajuste a ordem com as setas. O celular passa nessa ordem.
3. Cada jogador segura a carta para ver a palavra. A carta some quando você solta.
4. O impostor vê "VOCÊ É O IMPOSTOR". Pelas opções, ele também vê a categoria e uma dica.
5. Em ordem, cada jogador diz uma palavra ligada ao tema.
6. Depois do tempo de debate, o grupo vota em quem expulsar.
7. Se o grupo pegar o impostor, ele ainda tenta acertar a palavra secreta para virar o jogo.

### Pontuação

| Resultado | Pontos |
|---|---|
| Grupo pega o impostor | 1 ponto para cada jogador do grupo |
| Impostor escapa ou acerta a palavra | 2 pontos para cada impostor |

### Impostores automáticos

| Jogadores | Impostores |
|---|---|
| 3 a 6 | 1 |
| 7 a 10 | 2 |
| 11 ou mais | 3 |

Se você desligar a opção, escolhe o número de impostores à mão.

### Rodada surpresa

Opcional, desligada por padrão. Com ela ligada, cada rodada tem 1 chance em 10 de todos os jogadores serem impostores. As cartas ficam iguais às de uma rodada normal, então ninguém sabe.

Na votação aparece o botão "Ninguém sabe a palavra!":

| Situação | Resultado |
|---|---|
| Rodada surpresa e o grupo aperta o botão | Todos ganham 1 ponto |
| Rodada surpresa e o grupo expulsa alguém | Ninguém ganha ponto |
| Rodada normal e o grupo aperta o botão | Impostores ganham 2 pontos |

Para mudar a chance, altere `SURPRISE_CHANCE` em `web/index.html`.

## Conteúdo

- 10 categorias com 50 palavras cada: Comidas, Animais, Lugares, Profissões, Objetos, Esportes, Brasil, Países, Casa e Ações.
- Cada palavra tem 3 dicas. Com 2 ou 3 impostores, cada um recebe uma dica diferente.
- Em "Minhas palavras", você adiciona palavras suas, uma por linha. Para dar dicas, use `Palavra: dica1, dica2, dica3`.
- O jogo evita repetir as últimas 80 palavras.

## Como as atualizações chegam

O app busca o jogo em `https://ga-sr.github.io/theImpostor/` toda vez que abre e guarda uma cópia no celular.

- Com internet, você abre a versão mais nova.
- Sem internet, o app abre a última cópia salva.
- Na primeira vez sem internet, o app abre a cópia que veio dentro do APK.

Mudanças em `web/` chegam sozinhas ao app, sem APK novo. O GitHub Pages publica em cerca de 1 minuto, e o celular pega a mudança na próxima vez que abrir o app.

Você só precisa de um APK novo se mudar a parte Android: ícone, nome, permissões ou o código em `app/`.

## Estrutura

```
web/index.html          O jogo inteiro: HTML, CSS, JS e lista de palavras
app/                    App Android (WebView que carrega o jogo)
keystore/               Chave de assinatura, protegida por senha
.github/workflows/
  site.yml              Publica web/ no GitHub Pages
  apk.yml               Compila o APK e cria uma Release
```

## Editar palavras

Abra `web/index.html` e procure `const WORDS`. Cada palavra segue o formato:

```js
["Pizza", ["redonda", "forno", "fatia"]]
```

Para criar uma categoria, adicione uma chave nova com emoji e lista:

```js
"Filmes": { "e": "🎬", "w": [ ["Titanic", ["navio", "iceberg", "romance"]] ] }
```

Faça commit em `main`. O site e o app se atualizam sozinhos.

## Testar no computador

Abra `web/index.html` no navegador. Use o modo celular das ferramentas de desenvolvedor para ver como fica na tela do telefone.

## Gerar o APK

### Pelo GitHub (recomendado)

O workflow `Gerar APK` roda quando você muda arquivos em `app/`, ou quando você roda manualmente na aba Actions. O APK sai em:

- [Releases](https://github.com/ga-sr/theImpostor/releases), para baixar no celular
- branch `apk`, arquivo `Impostor.apk`

O workflow precisa do segredo `KEYSTORE_PASSWORD` em Settings > Secrets and variables > Actions.

### No computador

Requisitos: JDK 17 e Android SDK, ou Android Studio.

1. Abra a pasta do projeto no Android Studio.
2. Use Build > Build APK(s).

Sem a variável `KEYSTORE_PASSWORD`, o APK sai assinado com a chave de debug. Um APK de debug não instala por cima do app oficial.

## Assinatura

O arquivo `keystore/impostor-release.jks` é protegido por uma senha longa e aleatória. A senha fica só no segredo do GitHub e no backup do dono do projeto. Sem essa senha, ninguém assina um APK que instale por cima do app.

Guarde o backup da chave. Se você perder a chave, os usuários vão precisar desinstalar o app antes de instalar uma versão nova.

## Tecnologia

- Jogo: um arquivo HTML, sem dependências nem build. Fontes do Google Fonts.
- App: Kotlin, WebView, minSdk 24 (Android 7.0), targetSdk 34.
- CI: GitHub Actions, Gradle 8.7, Android Gradle Plugin 8.5.
