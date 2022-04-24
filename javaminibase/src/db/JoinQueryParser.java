package db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class JoinQueryParser implements IParser {
  /**
   * Parses the query and returns the query object
   *
   * @param inputQuery
   * @return Query object
   */
  @Override
  public IQuery parse(String inputQuery) throws IllegalArgumentException, IOException {
    /**
     * RDFDBNAME QUERYFILE NUMBUF INDEXOPTION
     */
    String[] tokens = inputQuery.split(" ");
    String dbName = tokens[0];
    String numBuf = tokens[2];
    String indexOption = tokens[3];
    if (tokens.length != 4) {
      throw new IllegalArgumentException("Number of arguments are not equal to 4");
    }
    String sf1, pf1, of1, cf1, jnp1, joso1, rsf1, rpf1, rof1, rcf1, ors1, oro1;
    String jnp2, joso2, rsf2, rpf2, rof2, rcf2, ors2, oro2;
    String so = null, snip = null, np = null;
    String[] lonp1, lonp2;
    String query = Files.readString(Paths.get(tokens[1]));
    String delims = "[()\\[\\]]+";
    tokens = query.split(delims);
    // for(String a:tokens)
    // {
    //   System.out.println(a.trim());
    // }

    if(tokens.length == 7 && tokens[0].contains("S") && tokens[1].contains("J") && tokens[2].contains("J"))
    {
      delims = "[,]";
      String[] str = tokens[3].split(delims);
      if (str.length == 4) {
        sf1 = str[0].trim();
        pf1 = str[1].trim();
        of1 = str[2].trim();
        cf1 = str[3].trim();
        System.out.println("Subject = "+sf1+" Predicate = "+pf1+" Object = "+of1+" Confidence = "+cf1);
      } else {
        throw new IllegalArgumentException("Wrong input");
      }

      str = tokens[4].split(delims);
      if (str.length == 10) {
        int i = 0;
        jnp1 = str[1].trim();
        joso1 = str[2].trim();
        rsf1 = str[3].trim();
        rpf1 = str[4].trim();
        rof1 = str[5].trim();
        rcf1 = str[6].trim();
        lonp1 = str[7].trim().split(":");
        ors1 = str[8].trim();
        oro1 = str[9].trim();
        System.out.println(jnp1+ " " + joso1 + " " + rsf1 + " "+ rpf1 + " " + rof1 + " "+ rcf1 + "" + Arrays.toString(lonp1) +
            " "+ ors1 +
            " " + oro1);
      } else {
        throw new IllegalArgumentException("Wrong input");
      }

      str = tokens[5].split(delims);
      if(str.length == 10)
      {
        jnp2 = str[1].trim();
        joso2 = str[2].trim();
        rsf2 = str[3].trim();
        rpf2 = str[4].trim();
        rof2 = str[5].trim();
        rcf2 = str[6].trim();
        lonp2 = str[7].trim().split(":");
        ors2 = str[8].trim();
        oro2 = str[9].trim();
        System.out.println(jnp2+ " " + joso2 + " " + rsf2 + " "+ rpf2 + " " + rof2 + " "+ rcf2 + "" + Arrays.toString(lonp2) + " "+ ors2 +
            " " + oro2);
      } else {
        throw new IllegalArgumentException("Wrong input");
      }

      str = tokens[6].split(delims);
      if(str.length == 3)
      {
        so = str[0].trim();
        snip = str[1].trim();
        np = str[2].trim();
        System.out.println("SortOrder = "+so+" SortNodeIDPos="+snip+" n_pages="+np);
      }
    } else {
      throw new IllegalArgumentException("Wrong input");
    }

    return new JoinQuery(
        dbName,
        indexOption,
        numBuf,
        sf1,
        pf1,
        of1,
        cf1,
        jnp1,
        joso1,
        rsf1,
        rpf1,
        rof1,
        rcf1,
        lonp1,
        ors1,
        oro1,
        jnp2,
        joso2,
        rsf2,
        rpf2,
        rof2,
        rcf2,
        lonp2,
        ors2,
        oro2,
        so,
        snip,
        np
    );
  }
}
