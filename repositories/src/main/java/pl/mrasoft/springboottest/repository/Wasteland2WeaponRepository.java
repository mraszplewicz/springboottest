package pl.mrasoft.springboottest.repository;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;
import pl.mrasoft.springboottest.model.QWasteland2Weapon;
import pl.mrasoft.springboottest.model.Wasteland2Weapon;
import pl.mrasoft.springboottest.repository.transaction.annotation.WeaponTx;
import pl.mrasoft.springboottest.repository.transaction.annotation.WeaponTxRO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class Wasteland2WeaponRepository {

    @PersistenceContext(unitName = "weapon")
    private EntityManager entityManager;

    @PersistenceContext(unitName = "weaponro")
    private EntityManager entityManagerRO;

    @WeaponTx
    public List<Wasteland2Weapon> findAllWithPagesRW(long offset, long limit) {
        return findAllWithPages(entityManager, offset, limit);
    }

    @WeaponTxRO
    public List<Wasteland2Weapon> findAllWithPages(long offset, long limit) {
        return findAllWithPages(entityManagerRO, offset, limit);
    }

    private List<Wasteland2Weapon> findAllWithPages(EntityManager em, long offset, long limit) {
        JPQLQuery query = new JPAQuery(em);
        QWasteland2Weapon wasteland2Weapon = QWasteland2Weapon.wasteland2Weapon;

        List<Wasteland2Weapon> weapons = query.from(wasteland2Weapon)
                .orderBy(wasteland2Weapon.type.desc(), wasteland2Weapon.name.desc())
                .limit(limit)
                .offset(offset)
                .list(wasteland2Weapon);

        return weapons;
    }

    @WeaponTx
    public Wasteland2Weapon save(Wasteland2Weapon entity) {
        return entityManager.merge(entity);
    }

}
