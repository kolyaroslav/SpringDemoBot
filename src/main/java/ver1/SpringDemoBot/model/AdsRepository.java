package ver1.SpringDemoBot.model;

import org.springframework.data.repository.CrudRepository;
//репозиторій для рекламних сповіщень.
public interface AdsRepository extends CrudRepository<Ads, Long> {

}