
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
    String csvContent = "name,email\nJohn,john@example.com";
    byte[] csvBytes = csvContent.getBytes();

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
        .contentType(MediaType.parseMediaType("text/csv"))
        .body(csvBytes);
}
```

--

### Fichier existant
- Adapté pour l'envoi de fichiers existants sur le serveur
- Utilisation de `ResponseEntity<Resource>`
- Idéal pour les fichiers statiques ou préexistants

```java
@GetMapping("/download")
public ResponseEntity<Resource> downloadFile() {
    Path filePath = Paths.get("chemin/vers/fichier.csv");
    Resource resource = new FileSystemResource(filePath);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
        .contentType(MediaType.parseMediaType("text/csv"))
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
    StreamingResponseBody stream = response -> {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(response))) {
            writer.write("name,email\n");//En-tête
            //Envoi des données du Stream vers le Writer
            stream.forEach(obj -> writer.write(obj.toString()));
        }
    };
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv")
        .contentType(MediaType.parseMediaType("text/csv"))
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