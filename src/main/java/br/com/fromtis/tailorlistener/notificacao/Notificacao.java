package br.com.fromtis.tailorlistener.notificacao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Notificacao {

    private String palavra;

    private String linha;

    private String arquivo;
}
