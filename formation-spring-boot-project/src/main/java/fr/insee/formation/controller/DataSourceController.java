   package fr.insee.formation.controller;

   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.RestController;
   import fr.insee.formation.service.DataSourceService;
   import java.util.HashMap;
   import java.util.Map;

   @RestController
   public class DataSourceController {
       @Autowired(required = false)
       private DataSourceService dataSourceService;

       @GetMapping("/datasource/info")
       public Map<String, Object> getDataSourceInfo() {
           Map<String, Object> result = new HashMap<>();
           if (dataSourceService != null) {
               result.put("status", "FOUND");
               result.put("service_class", dataSourceService.getClass().getSimpleName());
               result.put("message", dataSourceService.getInfo());
           } else {
               result.put("status", "NOT_FOUND");
               result.put("message", "Aucun DataSourceService disponible pour ce profil");
           }
           return result;
       }
   }