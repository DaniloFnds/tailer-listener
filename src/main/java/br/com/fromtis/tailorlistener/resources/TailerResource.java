package br.com.fromtis.tailorlistener.resources;

import br.com.fromtis.tailorlistener.entity.Config;
import br.com.fromtis.tailorlistener.listener.ExecutorTailor;
import br.com.fromtis.tailorlistener.listener.TailerListenerAdapter;
import br.com.fromtis.tailorlistener.notificacao.ClientNotificacao;
import br.com.fromtis.tailorlistener.notificacao.PalavraNotificacao;
import br.com.fromtis.tailorlistener.repository.ConfigDao;
import com.google.gson.Gson;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.ws.Response;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/tailer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TailerResource {

    private PalavraNotificacao palavraNotificacao;
    private final ClientNotificacao clientNotificacao;
    private final TailerListenerAdapter tailerListener;
    private final ConfigDao configDao;

    @Autowired
    public TailerResource(final PalavraNotificacao palavraNotificacao,
                          final ClientNotificacao clientNotificacao,
                          final TailerListenerAdapter tailerListener, final ConfigDao configDao) {
        this.palavraNotificacao = palavraNotificacao;
        this.clientNotificacao = clientNotificacao;
        this.tailerListener = tailerListener;
        this.configDao = configDao;
    }


    @GetMapping(path = "/config", produces = {MediaType.APPLICATION_JSON})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getConfig() {
        if (this.getConfigGravado().isPresent()) {
            return new Gson().toJson(this.getConfigGravado().get());
        }
        return "{Config nao encontrado}";
    }

    @GetMapping(path = "/config/palavras", produces = {MediaType.APPLICATION_JSON})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getPalavras() {
        if (this.getConfigGravado().isPresent() && this.getConfigGravado().get().getPalavra() != null) {
            return new Gson().toJson(this.getConfigGravado().get().getPalavra().getPalavras());
        }
        return "Nenhuma palavra registrada";
    }

    @PutMapping(path = "/config", produces = {MediaType.APPLICATION_JSON})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String novaConfig(@RequestBody String novaConfig) {
        Config configJson = new Gson().fromJson(novaConfig, Config.class);

        List<Config> configList = this.configDao.findAll();
        if (!CollectionUtils.isEmpty(configList)) {
            Config config = configList.get(0);
            configJson.setId(config.getId());
        }
        this.configDao.save(configJson);
        return "OK";
    }

    @GetMapping(path = "/start", produces = {MediaType.APPLICATION_JSON})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String startTailer() {
        Optional<Config> configGravado = getConfigGravado();
        if (configGravado.isPresent()) {
            Config config = configGravado.get();
            this.palavraNotificacao.setMensagem(config.getPalavra().getMensagem());
            this.palavraNotificacao.setPalavras(config.getPalavra().getPalavras());

            if (config.getArquivo() == null) {
                return "Arquivo nao configurado";
            }

            if (config.getPalavra() == null) {
                return "Nenhuma palavra registrada";
            }

            if (config.getPalavra() != null && config.getPalavra().getPalavras().isEmpty()) {
                return "Nenhuma palavra registrada";
            }

            if (ExecutorTailor.getInstance().existeTailer()) {
                ExecutorTailor.getInstance().tryStartTailer();
                Config config1 = this.configDao.findAll().get(0);

                Executors.newFixedThreadPool(1)
                        .execute(() -> {
                            System.out.println(Thread.currentThread().getName() + " - " + Thread.currentThread().getId());
                            ExecutorTailor.getInstance().init(config1.getArquivo(), palavraNotificacao,
                                    clientNotificacao, tailerListener, config);
                        });
            } else {
                Executors.newFixedThreadPool(1)
                        .execute(() -> {
                            System.out.println(Thread.currentThread().getName() + " - " + Thread.currentThread().getId());
                            ExecutorTailor.getInstance().init(config.getArquivo(), palavraNotificacao,
                                    clientNotificacao, tailerListener, config);
                        });
            }
        }

        return "OK";
    }

    @GetMapping(path = "/status", produces = {MediaType.APPLICATION_JSON})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getStatus() {
        return this.tailerListener.getStatus();
    }

    private Optional<Config> getConfigGravado() {
        if (CollectionUtils.isEmpty(this.configDao.findAll())) {
            return Optional.empty();
        }
        return Optional.of(this.configDao.findAll().get(0));
    }


}
