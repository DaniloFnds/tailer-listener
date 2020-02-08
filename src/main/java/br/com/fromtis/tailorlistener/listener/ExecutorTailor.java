package br.com.fromtis.tailorlistener.listener;

import br.com.fromtis.tailorlistener.entity.Config;
import br.com.fromtis.tailorlistener.notificacao.ClientNotificacao;
import br.com.fromtis.tailorlistener.notificacao.PalavraNotificacao;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.util.Optional;

public final class ExecutorTailor {

    private static ExecutorTailor instancia = new ExecutorTailor();
    private TailerListenerAdapter tailerListener;

    private ExecutorTailor() {
    }

    @Getter
    private Tailer tailer;

    public ExecutorTailor init(final String arquivoMonitor, final PalavraNotificacao palavraNotificacao,
                               final ClientNotificacao clientNotificacao,
                               final TailerListenerAdapter tailerListener, final Config config) {
        this.tailerListener = tailerListener;
        if (arquivoMonitor == null) {
            return this;
        }
        if(tailer != null) {

        }
        Tailer tailer = Tailer.create(new File(arquivoMonitor), tailerListener, config.getDelay(), true);
        this.tailer = tailer;
        tailer.run();
        return this;
    }

    public static ExecutorTailor getInstance() {
        if (instancia != null) {
            return instancia;
        } else {
            instancia = new ExecutorTailor();
        }
        return instancia;
    }


    public boolean existeTailer() {
        return Optional.ofNullable(this.tailer).isPresent();
    }

    public void tryStartTailer() {
        boolean running = this.tailerListener.isRunning(this.tailer);
        if(running) {
            this.tailer.stop();
        }
    }
}
