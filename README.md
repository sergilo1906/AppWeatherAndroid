Aquí tens la divisió de tasques per a les parts d'ubicació i API, i la part de disseny i notificacions:

---

### **1. Part d'ubicació i API (Responsable: Sergi)**

#### **A) Ubicació**
1. Configurar els permisos d'ubicació al fitxer `AndroidManifest.xml`:
   - Afegir `ACCESS_FINE_LOCATION` i `ACCESS_COARSE_LOCATION`.
   - Configurar opcions addicionals per dispositius amb Android 12 o superior.

2. Implementar la sol·licitud de permisos en temps d'execució:
   - Crear un mètode per demanar els permisos necessaris.
   - Gestionar la resposta de l'usuari (acceptar o rebutjar permisos).

3. Configurar `FusedLocationProviderClient`:
   - Inicialitzar el client d'ubicació.
   - Obtenir la ubicació aproximada del dispositiu (coordenades).

4. Utilitzar `Geocoder` per convertir coordenades al nom de la ciutat:
   - Obtenir el nom de la ciutat i mostrar-lo a la consola o interfície.

#### **B) API meteorològica**
1. Escollir i configurar l'API:
   - Registra't a l'API triada (per exemple, OpenWeatherMap o WeatherAPI).
   - Obtenir una `API key` i configurar-la al projecte de forma segura.

2. Realitzar la consulta a l'API:
   - Implementar una funció que enviï una petició a l'API usant les coordenades del dispositiu.
   - Processar la resposta per obtenir dades meteorològiques com temperatura, condicions i icones.

3. Mostrar les dades obtingudes:
   - Mostrar les dades meteorològiques a la interfície (nom de la ciutat, temperatura, condicions del temps, etc.).

---

### **2. Part de disseny i notificacions push (Responsable: Biel)**

#### **A) Disseny de la interfície**
1. Crear la interfície inicial:
   - Pantalla principal amb espais per mostrar:
     - Nom de la ciutat detectada.
     - Informació meteorològica (temperatura, condicions).
   - Un botó per introduir manualment una ciutat (opcional).
   - Barra inferior amb opcions com configuració i notificacions.

2. Millorar l'aspecte visual:
   - Afegir icones meteorològiques que canviïn segons les condicions (sol, pluja, etc.).
   - Assegurar que l'app s'adapti a diferents dispositius i orientacions.

3. Afegir navegació entre pantalles:
   - Pantalla de configuració per gestionar notificacions.
   - Pantalla de notificacions amb llistat d'alertes rebudes.

#### **B) Notificacions push**
1. Configurar Firebase al projecte:
   - Crear un projecte a Firebase Console.
   - Configurar Firebase Cloud Messaging (FCM) al projecte d'Android Studio.

2. Implementar la gestió de notificacions:
   - Crear una classe per gestionar els missatges de Firebase.
   - Configurar notificacions automàtiques segons la ciutat registrada.

3. Provar les notificacions:
   - Enviar notificacions de prova des de Firebase Console.
   - Assegurar que els missatges es mostren correctament segons la configuració de l'usuari.

---

Amb aquesta divisió, Sergi s'encarrega de la implementació tècnica de l'ubicació i la connexió amb l'API, mentre que Biel treballa en la part visual i de notificacions push. Si necessiteu més detalls sobre alguna tasca específica, avisa! 😊
