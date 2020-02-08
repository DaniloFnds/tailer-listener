package br.com.fromtis.tailorlistener.listener;

import br.com.fromtis.tailorlistener.notificacao.ClientNotificacao;
import br.com.fromtis.tailorlistener.notificacao.PalavraNotificacao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.inject.Named;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public final class TailerListenerImpl implements TailerListenerAdapter {

    private final PalavraNotificacao palavra;
    private final ClientNotificacao clientNotificacao;

    private Tailer tailers;

    @Autowired
    public TailerListenerImpl(final PalavraNotificacao palavra, final ClientNotificacao clientNotificacao) {
        this.palavra = palavra;
        this.clientNotificacao = clientNotificacao;
    }

    @Override
    public void init(final Tailer tailer) {
        this.tailers = tailer;
    }

    @Override
    public void fileNotFound() {
        //uma forma de apresentar o status
        System.out.println("rquivo nao encontrado");
    }

    @Override
    public void fileRotated() {

    }

    @Override
    public void handle(final String linha) {
        if (this.palavra != null) {
            for (final String palavra : this.palavra.getPalavras()) {
                final boolean contemPalavra = StringUtils.containsIgnoreCase(linha, palavra);
                if (contemPalavra) {
                    clientNotificacao.notificaPalavraEncontrada(palavra, linha);
                }
            }
        }
    }

    @Override
    public void handle(final Exception e) {
        //mostrear o status
        System.out.println(e);

        this.tailers.run();
    }

    @Override
    public String getStatus() {
        if (this.tailers == null) {
            return "{\"Nenhum Monitoramento ativado\"}";
        }

        StringBuilder sb = new StringBuilder();
        boolean running = isRunning(this.tailers);
        String status = running ? "\"Running\"" : "\"Stopped\"";
        sb.append("{\"Arquivo\": ").append("\"").append(this.tailers.getFile().getAbsolutePath()).append("\"").append(", \"Status\": ").append(status).append(System.lineSeparator());
        return sb.toString();
    }


    @Override
    public boolean isRunning(final Tailer tailer) {
        Method getRun = ReflectionUtils.findMethod(Tailer.class, "getRun");
        ReflectionUtils.makeAccessible(getRun);
        Boolean o = (Boolean) ReflectionUtils.invokeMethod(getRun, tailer);
        return o;
    }
}
