
## Envoi de fichiers
## avec Spring Web

--

### Fichier généré en mémoire
- Adapté pour des volumes de données réduits
- Utilisation de `ResponseEntity<byte[]>`
- Adapté pour les petits fichiers générés dynamiquement

```java
@GetMapping("/export")
public ResponseEntity<byte[]> exportCsv() {
    // 1. Génération du contenu
    String csvContent = "name,email\nJohn,john@example.com";

    // 2. Conversion en bytes (UTF-8)
    byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

    // 3. Configuration des en-têtes
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_PLAIN);
    headers.setContentDisposition(ContentDisposition.builder("attachment")
        .filename("users.csv")
        .build());

    // 4. Retour de la réponse
    return ResponseEntity.ok()
        .headers(headers)
        .body(csvBytes);
}
```

--

### Étapes pour l'envoi de fichiers

1. **Génération du contenu**
   ```java
   String csvContent = "name,email\nJohn,john@example.com";
   ```

2. **Conversion en bytes (UTF-8)**
   ```java
   byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);
   ```

3. **Configuration des en-têtes**
   ```java
   HttpHeaders headers = new HttpHeaders();
   headers.setContentType(MediaType.TEXT_PLAIN);
   headers.setContentDisposition(ContentDisposition.builder("attachment")
       .filename("data.csv").build());
   ```

4. **Retour de la réponse**
   ```java
   return ResponseEntity.ok().headers(headers).body(csvBytes);
   ```

--

### Fichier existant
- Adapté pour l'envoi de fichiers existants sur le serveur
- Utilisation de `ResponseEntity<Resource>`
- Idéal pour les fichiers statiques ou préexistants

```java
@GetMapping("/download")
public ResponseEntity<Resource> downloadFile() {
    // 1. Récupération du fichier
    Path filePath = Paths.get("chemin/vers/fichier.csv");
    Resource resource = new FileSystemResource(filePath);

    // 3. Configuration des en-têtes
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_PLAIN);
    headers.setContentDisposition(ContentDisposition.builder("attachment")
        .filename(resource.getFilename())
        .build());

    // 4. Retour de la réponse
    return ResponseEntity.ok()
        .headers(headers)
        .body(resource);
}
```

--

### Streaming
- Idéal pour les gros volumes de données
- Flux de données affiché dynamiquement
- Utilisation de `StreamingResponseBody`

```java
@GetMapping("/export-stream")
public ResponseEntity<StreamingResponseBody> exportCsvStream() {
    // 1. Génération du contenu via un flux
    StreamingResponseBody stream = response -> {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(response, StandardCharsets.UTF_8))) {
            writer.write("name,email\n"); // En-tête
            // Exemple : Envoi des données du flux vers le Writer
            List<String> data = Arrays.asList("John,john@example.com", "Jane,jane@example.com");
            data.forEach(line -> writer.write(line + "\n"));
        }
    };

    // 3. Configuration des en-têtes
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_PLAIN);
    headers.setContentDisposition(ContentDisposition.builder("attachment")
        .filename("users.csv")
        .build());

    // 4. Retour de la réponse
    return ResponseEntity.ok()
        .headers(headers)
        .body(stream);
}
```

--

## Récapitulatif : Envoi de fichiers avec Spring Web

| Méthode               | Utilisation                          
|-----------------------|--------------------------------------
| **`ResponseEntity<byte[]>`** | Petits fichiers générés en mémoire 
| **`ResponseEntity<Resource>`** | Fichiers existants sur le serveur 
| **`StreamingResponseBody`** | Gros volumes ou données dynamiques