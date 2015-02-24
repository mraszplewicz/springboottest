package pl.mrasoft.springboottest.repository;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;
import pl.mrasoft.springboottest.model.QWasteland2NPC;
import pl.mrasoft.springboottest.model.Wasteland2NPC;
import pl.mrasoft.springboottest.repository.transaction.annotation.NpcTx;
import pl.mrasoft.springboottest.repository.transaction.annotation.NpcTxRO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class Wasteland2NpcRepository {

    @PersistenceContext(unitName = "npc")
    private EntityManager entityManager;

    @PersistenceContext(unitName = "npcro")
    private EntityManager entityManagerRO;

    @NpcTx
    public List<Wasteland2NPC> findAllWithPagesRW(long offset, long limit) {
        return findAllWithPages(entityManager, offset, limit);
    }

    @NpcTxRO
    public List<Wasteland2NPC> findAllWithPages(long offset, long limit) {
        return findAllWithPages(entityManagerRO, offset, limit);
    }

    private List<Wasteland2NPC> findAllWithPages(EntityManager em, long offset, long limit) {
        JPQLQuery query = new JPAQuery(em);
        QWasteland2NPC qWasteland2NPC = QWasteland2NPC.wasteland2NPC;

        List<Wasteland2NPC> npcs = query.from(qWasteland2NPC)
                .orderBy(qWasteland2NPC.location.desc(), qWasteland2NPC.name.desc())
                .limit(limit)
                .offset(offset)
                .list(qWasteland2NPC);

        return npcs;
    }

    @NpcTx
    public Wasteland2NPC save(Wasteland2NPC entity) {
        return entityManager.merge(entity);
    }

}
