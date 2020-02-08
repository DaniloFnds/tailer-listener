package br.com.fromtis.tailorlistener.notificacao;

import br.com.fromtis.tailorlistener.entity.Config;
import br.com.fromtis.tailorlistener.repository.ConfigDao;
import com.google.gson.Gson;
import org.glassfish.jersey.internal.guava.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;

@Service
public class ClientNotificacao {

    private final ConfigDao configDao;
    private final RestTemplate restTemplate;

    @Autowired
    public ClientNotificacao(final ConfigDao configDao, final RestTemplate restTemplate) {
        this.configDao = configDao;
        this.restTemplate = restTemplate;
    }

    public void notificaPalavraEncontrada(final String palavra, final String linha) {
        Config config = this.configDao.findAll().get(0);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Notificacao> httpEntity = new HttpEntity<>(new Notificacao(palavra, linha, config.getArquivo()), httpHeaders);
        restTemplate.postForEntity(config.getUrl(), httpEntity, String.class);
    }
}
