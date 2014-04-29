package net.sourceforge.jwbf.mediawiki.actions.queries;

import static com.github.dreamhead.moco.Moco.and;
import static com.github.dreamhead.moco.Moco.by;
import static com.github.dreamhead.moco.Moco.eq;
import static com.github.dreamhead.moco.Moco.query;
import static com.github.dreamhead.moco.Moco.uri;
import static org.junit.Assert.assertEquals;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jwbf.AbstractIntegTest;
import net.sourceforge.jwbf.GAssert;
import net.sourceforge.jwbf.TestHelper;
import net.sourceforge.jwbf.mediawiki.actions.MediaWiki;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;

import org.junit.Test;

import com.github.dreamhead.moco.RequestMatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@Slf4j
public class TemplateUserTitlesIntegTest extends AbstractIntegTest {

  RequestMatcher embeddedinTwo = AbstractIntegTest.onlyOnce(and(by(uri("/api.php")), //
      eq(query("eicontinue"), "10|Babel|37163"), //
      eq(query("action"), "query"), //
      eq(query("format"), "xml"), //
      eq(query("eilimit"), "50"), //
      eq(query("einamespace"), "2"), //
      eq(query("eititle"), "Template:Babel"), //
      eq(query("list"), "embeddedin") //
      ));

  RequestMatcher embeddedinThree = AbstractIntegTest.onlyOnce(and(by(uri("/api.php")), //
      eq(query("eicontinue"), "10|Babel|39725"), //
      eq(query("action"), "query"), //
      eq(query("format"), "xml"), //
      eq(query("eilimit"), "50"), //
      eq(query("einamespace"), "2"), //
      eq(query("eititle"), "Template:Babel"), //
      eq(query("list"), "embeddedin") //
      ));

  RequestMatcher embeddedinOne = AbstractIntegTest.onlyOnce(and(by(uri("/api.php")), //
      eq(query("action"), "query"), //
      eq(query("format"), "xml"), //
      eq(query("eilimit"), "50"), //
      eq(query("einamespace"), "2"), //
      eq(query("eititle"), "Template:Babel"), //
      eq(query("list"), "embeddedin") //
      ));

  @Test
  public void test() {

    // GIVEN
    server.request(embeddedinThree).response(TestHelper.anyWikiResponse("embeddedin_3.xml"));
    server.request(embeddedinTwo).response(TestHelper.anyWikiResponse("embeddedin_2.xml"));
    server.request(embeddedinOne).response(TestHelper.anyWikiResponse("embeddedin_1.xml"));
    MediaWikiBot bot = new MediaWikiBot(host());

    // WHEN
    TemplateUserTitles testee = new TemplateUserTitles(bot, "Template:Babel", MediaWiki.NS_USER);
    List<String> resultList = testee.getCopyOf(15);

    // THEN
    ImmutableList<String> expected = ImmutableList.of("User:AxelBoldt", "User:Piotr Gasiorowski",
        "User:RobLa", "User:Taral", "User:Ap", "User:Yargo", "User:Joakim Ziegler", "User:Snorre",
        "User:LA2", "User:Codeczero", "User:Jkominek", "User:Oliver", "User:Walter",
        "User:Poslfit", "User:Qaz");
    GAssert.assertEquals(expected, ImmutableList.copyOf(resultList));
    assertEquals(resultList.size(), ImmutableSet.copyOf(resultList).size());

  }

}