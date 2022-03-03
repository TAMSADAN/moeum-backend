package moeum.moeum.restApiController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RestApi {

    @Value("${apiKey}")
    private String apiKey;

    @GetMapping("/nft-assets")
    public String getNFTs(@RequestParam("addr") String addr, @RequestParam("chain") String chain)
            throws JsonProcessingException {

        HashMap<String, Object> result = new HashMap<String, Object>();
        String jsonInString = "";

        try {

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setConnectTimeout(10000);    //타임아웃 설정 10초
            factory.setReadTimeout(10000);       //타임아웃 설정 10초
            RestTemplate restTemplate = new RestTemplate(factory);

            HttpHeaders header = new HttpHeaders();
            header.set("X-API-Key", apiKey);
            header.set("Accept", "application/json");
            HttpEntity<?> entity = new HttpEntity<>(header);

            String url = "https://deep-index.moralis.io/api/v2";
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(url + "/" + addr + "/nft?chain=" + chain).build();

            //이 한줄의 코드로 API를 호출해 Map type으로 전달 받는다.
            ResponseEntity<Map> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, Map.class);
            result.put("statusCode", resultMap.getStatusCodeValue());   //http status code 확인
            result.put("header", resultMap.getHeaders());   //헤더 정보 확인
            result.put("body", resultMap.getBody());    //실제 데이터 정보 확인

            //데이터를 제대로 전달 받았는지 확인 string 형태로 파싱해줌
            ObjectMapper mapper = new ObjectMapper();
            jsonInString = mapper.writeValueAsString(resultMap.getBody());

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            result.put("statusCode", e.getRawStatusCode());
            result.put("body", e.getStatusText());
            System.out.println("HttpClintErrorExceoption or HttpServerErrorException occur");
            System.out.println(e.toString());

        } catch (Exception e) {
            result.put("StatusCode", "999");
            result.put("body", "Exception error");
            System.out.println(e.toString());
        }

        return jsonInString;
    }
}
