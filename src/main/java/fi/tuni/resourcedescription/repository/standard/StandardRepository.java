package fi.tuni.resourcedescription.repository.standard;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import fi.tuni.resourcedescription.model.rcp.standard.Standard;

@Repository
public interface StandardRepository extends JpaRepository<Standard, Integer> {
  @Query(value = "SELECT count(s.stdId) as nbrStd FROM Standards s", nativeQuery = true)
  Integer getTotalNumberOfStandards();

  @Query(value = "SELECT\n" +
    "\ts.*,\n" +
    "    b.*,\n" +
    "   (SELECT COUNT(id) FROM RDs WHERE xmlexists('$d//*[contains(@id, $stdId)]' passing content as \"d\", CAST(s.stdId as VARCHAR) as \"stdId\")) as nbrRDs\n" +
    "FROM\n" +
    "\tStandards s,\n" +
    "    Standard_Bodies b \n" +
    "WHERE\n" +
    "\ts.bodyId=b.id \n" +
    "ORDER BY b.bodyId, s.stdId;", nativeQuery = true)
  List<Standard> getAllStandards();
}
