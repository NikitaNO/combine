import java.io.{File, FileInputStream}
import java.util.Properties
val p = new Properties()
//p.load(new FileInputStream(new File("D:\\aggregator\\lift_basic\\default.properties")))
  p.get("last_date").toString