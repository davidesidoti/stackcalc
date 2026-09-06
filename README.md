# Stack Calc

Calcolatrice in-game per Minecraft 26.2 / Fabric. Ti dice quanti stack e shulker box
sono un numero, e sa fare i conti.

## Cosa serve prima di partire

- **JDK 25** (obbligatorio, la 26.2 non compila con meno). Prendi Temurin 25 da adoptium.net.
- IntelliJ IDEA Community (consigliato) oppure solo la riga di comando.

Verifica con `java -version`, deve dire 25.

## Dove mettere questa cartella

**NON** dentro la cartella dell'istanza CurseForge. Questo e' il progetto sorgente,
mettilo dove ti pare, tipo:

```
C:\Users\sidot\dev\stackcalc
```

Il jar compilato invece va qui:

```
C:\Users\sidot\curseforge\minecraft\Instances\Enhanced Vanilla\mods\
```

## Build

Apri PowerShell nella cartella del progetto:

```powershell
.\gradlew.bat build
```

La prima volta ci mette qualche minuto perche' scarica Minecraft e le dipendenze.

Il jar esce in `build\libs\stackcalc-1.1.0.jar`. Copia **quello**, non quello che
finisce per `-sources.jar`.

## Test senza reinstallare ogni volta

```powershell
.\gradlew.bat runClient
```

Ti apre un Minecraft con la mod gia' dentro. Molto piu' comodo che copiare il jar
a mano ogni volta che cambi una riga.

## Installazione nell'istanza

1. Assicurati che l'istanza "Enhanced Vanilla" sia su **Minecraft 26.2 con Fabric Loader 0.19.3+**
2. Scarica **Fabric API 0.159.0+26.2** da Modrinth e mettilo in `mods\`
3. Copia `stackcalc-1.1.0.jar` in `mods\`
4. Avvia

## Come si usa

### GUI
Premi `=` in gioco. Si apre la calcolatrice. Il tasto si cambia da Opzioni > Comandi.

- Scrivi e il risultato si aggiorna da solo
- Il bottone a destra cicla la dimensione stack: 64 / 16 / 1
- Invio copia il risultato negli appunti
- Esc chiude

### Comandi chat
```
/stack 130            ->  130 = 2 stack + 2 item
/stack 130 16         ->  usa stack da 16
/stack calc 5s + 12   ->  332 = 5 stack + 12 item
```

## Sintassi delle espressioni

Operatori: `+` `-` `*` (o `x`) `/` `%` `^`, parentesi, moltiplicazione implicita `2(3+4)`.

| Suffisso | Vale | Esempio |
|---|---|---|
| `s`, `st`, `stack` | 64 | `5s` = 320 |
| `h`, `sh`, `shulker`, `c`, `chest` | 1728 | `2h` = 3456 |
| `dc` | 3456 | `1dc` = 3456 |
| `k` | 1.000 | `10k` = 10000 |
| `mi`, `mil` | 1.000.000 | `2mi` = 2000000 |

Esempi che funzionano:

```
130            = 130     -> 2 stack + 2 item
5s + 12        = 332     -> 5 stack + 12 item
2h - 3s        = 3264    -> 1 shulker + 24 stack
10k            = 10000   -> 5 shulker + 21 stack + 16 item
2(3+4)         = 14      -> 14
```

## Se qualcosa esplode

**`EditBox` non compila**: il costruttore cambia firma spesso. Guarda l'errore e
togli o aggiungi l'ultimo parametro `Component`.

**`GuiGraphicsExtractor` non trovato**: e' nuovo nella 26.x. Ctrl+click su `Screen`
in IntelliJ e controlla la firma vera di `extractRenderState`.

**Versioni sbagliate**: i numeri in `gradle.properties` invecchiano in fretta.
Controlla su https://fabricmc.net/develop e aggiorna li'.

## Note sulle dipendenze

Con il plugin `net.fabricmc.fabric-loom` (versioni NON offuscate, 26.1+) si usa
`implementation`, non `modImplementation`. Le configurazioni `mod*` esistono solo
nel plugin `-remap` per le versioni vecchie, perche' servono a rimappare.

## Client side only

Dalla 1.1.0 la mod e' interamente client-side: `environment: "client"` nel manifest e
`/stack` registrato via `ClientCommandRegistrationCallback`. Non va installata sul
server, funziona anche su server vanilla.

## Traduzioni

Niente stringhe hardcoded, tutto in `src/main/resources/assets/stackcalc/lang/`.
Default inglese (`en_us.json`), italiano in `it_it.json`. Per aggiungere una lingua
basta copiare `en_us.json` e tradurre i valori.

## Pubblicazione

La descrizione pronta per CurseForge e Modrinth sta in `DESCRIPTION.md`.
Il logo 512x512 per la pagina progetto e' `logo.png` nella root.