# GrafGenerator
Generator neusmerjenih grafov za 3. nalogo pri predmetu APS2.
Za podani n ustvari povezan graf z n vozlišči in približno 2n povezavami.

## Uporaba
Generator se uporablja z ukazom:
```
java GrafGen n [stikala]
```
kjer so za stikala možnosti:
- `-a/--algoritem g`, kjer je g ime algoritma, privzeto je "default", lahko še "alt"
- `-p/--povezav n`, kjer je n **približno** število povezav v grafu
- `-np/--nepovezan`, program ne poskrbi, da je izhodni graf povezan
- `-ns/--nesortiraj`, program ne sortira seznama povezav v leksikografskem vrstnem redu

## TODO
- **Drevesa**
- **Optimizacija**
## Pomoč
Odprt sem za kakršno koli pomoč, nasvete in predloge. Lahko odpreš nov issue ali pa sprogramiraš sam in ustvariš pull request.
