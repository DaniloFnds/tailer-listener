package br.com.fromtis.tailorlistener.notificacao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;

import javax.inject.Named;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Service
@Getter @Setter
@Entity
@Table(name = "TB_PALAVRA_NOTIFICACAO")
@AttributeOverride(name = "id", column = @Column(name = "ID_PALAVRA_NOTIFICACAO"))
public class PalavraNotificacao extends AbstractPersistable<Integer> {

    @ElementCollection()
    private Set<String> palavras;

    @Column(name = "MENSAGEM")
    private String mensagem;

}
