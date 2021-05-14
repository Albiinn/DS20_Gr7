# DS20_Gr7

DS20_Gr7 eshte nje projekt ne te cilin kemi shkruar nje console program i cili pranon komanda permes
argumenteve. Ky program do ti analizoje argumentet, dhe varesisht nga permbajtja e tyre do te
ekzekutoje ndonjeren prej komandave te specifikuara.
Programi eshte i shenuar ne gjuhen programuese Java

Programin mund ta shkarkoni si zip:
[ds.zip](https://github.com/Albiinn/DS20_Gr7/files/6480667/ds.zip), ndersa pershkrimin e komandave mund ta gjeni me poshte.


## Pershkrimi i komandave:

 Ne kete program perfshihen gjithsej 3 algoritme te kriptimit: beale cipher, tap-code cipher dhe komanda case.
- Beale cipher punon sipas parimit: cdo shkronje ne plaintext eshte e zevendesuar me nje numer, numer ky i cili paraqet pozicionin e fjales e cila fillon me ate shkronje ne nje liber.
- Tap-code cipher thuhet që është përdorur nga ushtarët e burgosur gjatë luftës në Vietnam. 
 Shkronjat janë organizuar në një tabelë 5 × 5. Për ta transmetuar një shkronjë, është trokitur aq herë sa e ka rreshtin,
 e pastaj aq herë sa e ka kolonën. Meqe alfabeti latin ka 26 shkronja dhe tabela permbane 5 x 5 = 25 shkronja, atehere shkronja “C” është përdorur në vend të shkronjës “K”.
- Case komanda e konverton tekstin ne madhesine e dhene, e cila mund te jete: lower, upper, capitalize, inverse, alternating, sentence.

## Hapat per ekzekutimin e programit:
### Faza 1

1. Hapeni nje folder ne nje direction psh ne Desktop, dhe i vendosni emrin CryptoAlgorithms.
2. Tek ky folder, hapini 4 Text Document ku secilit file i vendosni emrin e klases perkatese dhe i ndryshoni file extension-in nga .txt ne .java.
3. Kopjoni kodin burimor ne repo dhe permes nje editori psh Notepad, seciles klase ia vendosni kodin perkates.
4. Hapeni terminalin (cmd apo git bash) ne Desktop dhe shenoni javac CryptoAlgorithms/ds.java ne menyre qe te beni compile programin.

- Nese doni te enkriptoni ne komanden beale, atehere shenoni:\
 java CryptoAlgorithms/ds beale encrypt &lt;book> &lt;plaintext>\
 Per dekriptim shenoni:\
 java CryptoAlgorithms/ds beale decrypt &lt;book> &lt;ciphertext>

- Nese deshironi te enkodoni me komanden tap-code, shenoni:\
 java CryptoAlgorithms/ds tap-code encode &lt;text>\
 Per dekodim kemi:\
 java CryptoAlgorithms/ds tap-code decode &lt;text>;

- Nese deshironi te konvertoni tekstin me komanden case, shenoni:\
 java CryptoAlgorithms/ds case lower &lt;text>\
 java CryptoAlgorithms/ds case upper &lt;text>\
 java CryptoAlgorithms/ds case capitalize &lt;text>\
 java CryptoAlgorithms/ds case inverse &lt;text>\
 java CryptoAlgorithms/ds case alternating &lt;text>\
 java CryptoAlgorithms/ds case sentence &lt;text>
 
 ### Faza 2
 
 1. Ndiqni hapin 1 te Fazes 1
 2. Tek ky folder, hapini 10 Text Document ku secilit file i vendosni emrin e klases perkatese dhe i ndryshoni file extension-in   nga .txt ne .java.
 3. Ndiqni hapat 3-4 te Fazes 1
 
 - Per te krijuar nje cift te celesave publik/privat te RSA me emrat <name>.xml dhe <name>.pub.xml brenda
direktoriumit të celesave keys, atehere shenoni:\
 java CryptoAlgorithms/ds create-user &lt;name>
 
 - Per te larguar te gjithe celesat ekzistues te shfrytezuesit, shenoni:\
 java CryptoAlgorithms/ds delete-user &lt;name>
 
 - Per te eksportuar celesin publik ose privat te shfrytezuesit nga direktoriumi i celesave, shenoni:\
 java CryptoAlgorithms/ds export-key &lt;public | private> &lt;name> [file]
 
 - Per te importuar celesin publik ose privat te shfrytezuesit nga shtegu i dhene e per te vendosur ne direktoriumin
e celesave, shenoni:\
java CryptoAlgorithms/ds import-key &lt;name> &lt;path> 

- Per te shkruar nje mesazh te enkriptuar te dedikuar per nje shfrytezues, shenoni:\
java CryptoAlgorithms/ds write-message &lt;name> &lt;message> [file]

- Per te dekriptuar dhe shfaqur ne console mesazhin e enkriptuar, shenoni:\
java CryptoAlgorithms/ds read-message &lt;encrypted-message>

### Faza 3

Per te ekzekutuar kete faze, e cila gjithashtu permbane edhe dy fazat tjera (Fazen 1 dhe fazen 2, pra tere programin), ju duhet te download-oni dhe install-oni veglen softwerike Maven.
Hapat per instalimin dhe konfigurimin e kesaj vegle, mund ti ndiqni ne linkun: https://www.youtube.com/watch?v=3ODSQ0EpoQI.
Pasi te keni konfiguruar veglen Maven, ndiqni hapat ne vijim:
1. Hapeni terminalin (cmd apo git bash) ne Desktop
2. Shenoni "mvn archetype:generate -DgroupId=JWTs -DartifactId=DS20_Gr7 -DarchetypeArtifactId=maven-archetype-quickstart-DinteractiveMode=false", ku -DgroupId paraqet emrin e package dhe -DartifactId emrin e projektit.
3. Ne Desktop eshte krijuar projekti me emrin DS20_Gr7. Klikoni ne kete projekte dhe ne file-n pom.xml, se pari fshini pjesen e kodit qe fillon me &lt;dependencies> dhe mbaron me &lt;/dependencies>. Pastaj, kopjoni kodin ndermjet rreshtit 17 dhe 72 te file-s pom.xml qe ndodhet ne github dhe vendoseni tek file pom.xml i projektit te krijuar.
4. Ne projektin e krijuar shkoni tek pathi src\main\java\JWTs dhe fshijeni file-n App.java.
5. Tani, krijoni 12 txt file me emrat perkates sikurse qe jane ne github, dhe nderroni extensionin nga .txt ne .java.
6. Kopjoni kodin burimor nga github dhe venosini ne java file-t perkatese.
7. Hapeni terminalin ne projektin e krijuar dhe shenoni "mvn assembly:assembly -DdescriptorId=jar-with-dependencies"
8. Eshte shtuar nje follder i ri ne projekt me emrin "target". Hyni ne kete follder dhe .jar file-n me emer "DS20_Gr7-1.0-SNAPSHOT-jar-with-dependencies" emertone ne "ds.jar".
9. Ne terminalin e hapur, tani mund ti beni run cilat do komanda "java -jar target/ds.jar &lt;komandat me argumente>

## Shembuj nga rezultatet e ekzekutimit:
### Komanda beale:

java CryptoAlgorithms/ds beale encrypt libri.txt "pershendetje"\
3 10 30 23 14 10 4 15 10 19 29 10

java CryptoAlgorithms/ds beale decrypt libri.txt "3 10 30 23 14 10 4 15 10 19 29 10"\
pershendetje

### Komanda tap-code:

java CryptoAlgorithms/ds tap-code encode "neser"\
... ...&ensp;. .....&ensp;.... ...&ensp;. .....&ensp;.... ..

java CryptoAlgorithms/ds tap-code decode "... ...&ensp;. .....&ensp;.... ...&ensp;. .....&ensp;.... .."\
neser

### Komanda case:

java CryptoAlgorithms/ds case lower "Pershendetje nga FIEK!"\
pershendetje nga fiek!

java CryptoAlgorithms/ds case upper "Pershendetje nga FIEK!"\
PERSHENDETJE NGA FIEK!

java CryptoAlgorithms/ds case capitalize "Pershendetje nga FIEK!"\
Pershendetje Nga Fiek!

java CryptoAlgorithms/ds case inverse "Pershendetje nga FIEK!"\
pERSHENDETJE NGA fiek!

java CryptoAlgorithms/ds case alternating "Pershendetje nga FIEK!"\
pErShEnDeTjE NgA FiEk!

java CryptoAlgorithms/ds case sentence "pershendetje, Fjalia E pare. FJALIA E DYTE! fjAlia E trEte."\
Pershendetje, fjalia e pare. Fjalia e dyte! Fjalia e trete.
