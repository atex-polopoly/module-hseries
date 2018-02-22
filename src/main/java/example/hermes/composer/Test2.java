package example.hermes.composer;

import com.atex.plugins.structured.text.Note;
import com.atex.plugins.structured.text.StructuredText;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class Test {

    private static HashMap<String, String> embedMap = new HashMap<String, String>();

    public static void main(String[] args) {

        StructuredText st = new StructuredText();
        st.setText("<p>Ed Balls has said he will increase the minimum wage and the top rate of income tax and extend child benefit curbs in his first Budget if Labour wins power. The shadow chancellor told</p>\n" +
                "<a class=\"p-smartembed\" data-attr-f=\"3x2\" data-attr-q=\"0.3\" data-attr-w=\"400\" data-onecms-id=\"onecms:963b374f-5419-4ee7-a684-9be2abcffa54\" data-onecms-type=\"image\" href=\"javascript:window.parent.actionEventData({$contentId:'onecms:963b374f-5419-4ee7-a684-9be2abcffa54', $action:'view', $target:'work'})\" polopoly:contentid=\"onecms:963b374f-5419-4ee7-a684-9be2abcffa54\"><img src=\"/image/onecms:963b374f-5419-4ee7-a684-9be2abcffa54/14+&gt;&gt;&amp;&amp;AA.jpg?f=3x2&amp;w=400&amp;q=0.3\" /></a>\n" +
                "\n" +
                "<p>Labour&#39;s conference the party had &quot;more work to do&quot; to persuade people it can deliver the change he said people wanted. He said he would act swiftly after the election to reverse housing benefit cuts and boost jobs for young people. And he hinted that Labour would be prepared to accept Heathrow expansion. In his last conference address before next year&#39;s election, Mr Balls said Labour had learnt from its &quot;past mistakes&quot; and would not &quot;flinch&quot; from tough decisions if it regained power. Anticipating his first Budget, which he would be expected to deliver in the summer of 2015 if Labour was elected, Mr Balls said his priorities would be: Mr Balls said Labour was serious about &quot;balancing the books&quot; in the next Parliament and would not &quot;make any promises it cannot keep or afford&quot;. &quot;The country is crying out for change,&quot; he said. &quot;But we have more work to do to show Labour can deliver the change that people want to see. Analysis by economics editor Robert Peston Ed Balls has a difficult trick to pull off at Labour&#39;s conference in Manchester. He wants to be seen to be austere and fiscally righteous - so that investors in Britain do not become anxious that Britain&#39;s high public sector deficit, which is currently running at around 6% of national income, would persist for many years yet. To put Britain&#39;s deficit into perspective, that gap between revenues and spending is about 50% higher than France&#39;s - which has a lower credit rating than the UK&#39;s and is widely seen to be in a much bigger economic mess than this country. But he cannot be seen to be as austere as Chancellor George Osborne because&nbsp; then there would be little reason to vote Labour. Read Robert&#39;s full blog here. &quot;To show that we have learned from our time in government, that we will make the tough decisions we need to get the deficit down and that we can change our economy and make it work for working people.&quot; As part of what he said was a &quot;fully costed&quot; programme, he announced that the value of child benefits would continue to fall in real terms for the first two years of a Labour government. Under his plans, child benefit payments would not rise in line with inflation but by a fixed rate of 1% per year until 2017. The policy is already in place until 2016, having been announced by the coalition, but Labour&#39;s move would see it continue for another year. Millions of households which receive the benefit would be affected by the move. Labour has repeatedly criticised the coalition&#39;s benefit cuts, including Chancellor George Osborne&#39;s decision to remove child benefit for higher earning households, which Mr Balls said in 2013 created &quot;huge unfairness&quot;. But Mr Balls said Labour was set to inherit an annual budget deficit of &pound;75bn if it regains power and as a result will not be able to reverse most of the cuts and will have to introduce some of its own. He said: &quot;We will have to make other decisions which I know will not be popular with everyone. &quot;I want to see child benefit rising again in line with inflation in the next parliament, but we will not spend money we cannot afford. &quot;So for the first two years of the next parliament, we will cap the rise in child benefit at 1%. It will save &pound;400m in the next Parliament. And all the savings will go towards reducing the deficit.&quot; Mr Balls said Labour&#39;s deficit-cutting approach, which aims to eliminate the current budget surplus by 2020, would be &quot;fairer&quot; than the Conservatives because they would reinstate the 50% top rate of income tax. He also announced plans for a 5% pay cut for government ministers with their pay to be frozen until the deficit has been cleared. And he suggested that a future Labour government would &quot;resolve&quot; the issue of new airport capacity in the south of England and not &quot;kick the issue into the long grass&quot;. He said a decision must be taken whatever the outcome of the Davies commission into future options - which is looking at expanding either Heathrow and Gatwick. A Treasury source said the child benefit move would only yield &pound;120m, suggesting Labour&#39;s figures were based on historical rather than current rates of inflation. Culture Secretary Sajid Javid said Labour voted against the original child benefit cap but now were saying that &quot;it did not go far enough&quot;. Analysis by personal finance reporter Kevin Peachey Much of the focus at the start of 2013 - when big changes were made to child benefit - was on those high-income parents no longer entitled to the money. But arguably more significant was the fact that lower-earning mums and dads were taking a hit in real terms too. The benefit was frozen for three years from 2010 and is not rising in line with the cost of living for another two. Remember, it is not the only benefit witnessing a 1% cap. Others include maternity pay and jobseeker&#39;s allowance. Labour&#39;s proposals would tighten the financial squeeze on parents. But only when we see all parties&#39; tax and benefit proposals in full will it be clear who is exerting the greatest pressure on our wallets and purses. And child poverty campaigners said it would leave the typical family &pound;400 a year worse off by 2017. &quot;We urge the shadow chancellor to reconsider so that children and their already struggling families do not suffer even more unnecessary hardship,&quot; said Children&#39;s Society boss Matthew Reed. The CBI business group said Mr Balls had set out &quot;some of the foundations&quot; for long-term growth but expressed concerns about attempts to &quot;politicise&quot; decisions about the minimum wage. &quot;Attempting to fix future levels now is finger-in-the-air economics which takes no account of the economic realities at that time, nor the ability of firms to pay,&quot; said its director general John Cridland. Unite general secretary Len McCluskey praised the bulk of Mr Balls&#39; speech but said the child benefit freeze was a &quot;throwaway line designed to demonstrate some kind of toughness&quot;. And UKIP said Labour was &quot;promising the earth without doing the sums&quot;. Mr Balls was in a cuts controversy of a different kind on Sunday, when he accidentally elbowed a journalist in the eye during a charity football match. He has defended the coming together as an accident and said there was no malice intended.</p>\n");

        StructuredText d = replaceEmbedTags(st);

        System.out.println(d.getText());
    }

    private static StructuredText replaceEmbedTags (StructuredText sourceBody){

        StructuredText parsedBody = sourceBody;

		/*  SMART EMBED SOURCE
		<a class="p-smartembed" data-attr-f="3x2" data-attr-q="0.3" data-attr-w="400" data-onecms-id="onecms:ecfbd6b4-3a7e-4273-a4ea-67a127356d09" data-onecms-type="image" href="javascript:window.parent.actionEventData({$contentId:'onecms:ecfbd6b4-3a7e-4273-a4ea-67a127356d09', $action:'view', $target:'work'})" polopoly:contentid="onecms:ecfbd6b4-3a7e-4273-a4ea-67a127356d09">
			<img src="/image/onecms:ecfbd6b4-3a7e-4273-a4ea-67a127356d09/909e0966-885c-4f4c-a8c9-e21cafc2f390?f=3x2&amp;w=400&amp;q=0.3" />
		</a>
		 */

        if(sourceBody.getText().contains("p-smartembed")){

            String sourceText =  sourceBody.getText();

            org.jsoup.nodes.Document doc = Jsoup.parse(sourceText);
            Elements smartEmbedElements = doc.getElementsByAttributeValue("class", "p-smartembed");
            int i = 1;
            for (org.jsoup.nodes.Element embedElement:smartEmbedElements) {

                String dataonecmsid = embedElement.attr("data-onecms-id");
                embedMap.put(String.valueOf(i), dataonecmsid);
                formatEmbedNode(embedElement, i);
                embedElement.remove();


                if(parsedBody.getNotes() == null)
                    parsedBody.setNotes( new HashMap<String, Note>());

                Note note = new Note();
                note.setText("Embedded Content ");
                note.setCreated("");
                note.setModified("");
                note.setUser("");

                int noteCounter = i-1;
                parsedBody.getNotes().put("note-"+noteCounter, note);

                i++;
            }

            parsedBody.setText(doc.outerHtml().replaceAll("\n",""));
            return parsedBody;

        }else
            return sourceBody;

    }


    private static org.jsoup.nodes.Node formatEmbedNode(org.jsoup.nodes.Node currentElement, int i){

		/* NOTES RESULT

                <span data-atex-uat=\"{ }\" data-atex-id=\"cmd\" data-atex-name=\"ET\">&thinsp;</span>
                <span data-atex-af=\"$ID/PoynterOSTextThreeL Roman\" data-atex-fs=\"Normal\" data-atex-cfill=\"Color/Black\">
                               <span class=\"x-atex-note\" x-atex-user=\"\" x-atex-created=\"\" x-atex-modified=\"\">Embedded content </span>
                </span>
                <span data-atex-cstyle=\"EMBED2\" data-atex-af=\"$ID/TitlingGothicFB Normal Black\" data-atex-fs=\"Black\" data-atex-cfill=\"Color/Black\">
                </span>
                <span data-atex-uat=\"{ }\" data-atex-id=\"cmd\" data-atex-name=\"EI\" data-atex-v1=\"2\" data-atex-cstyle=\"EMBED2\">&nbsp;</span>
                <span data-atex-cstyle=\"EMBED2\" data-atex-af=\"$ID/TitlingGothicFB Normal Black\" data-atex-fs=\"Black\" data-atex-cfill=\"Color/Black\">
                </span>

		 */

        String index = String.valueOf(i);

        org.jsoup.nodes.Node span1Node = new Element(Tag.valueOf("span"),"");
        span1Node.attr("ata-atex-uat","{ }");
        span1Node.attr("ata-atex-id","cmd");
        span1Node.attr("ata-atex-name","ET");
        ((Element) span1Node).text(Entities.getCharacterByName("thinsp").toString());


        org.jsoup.nodes.Node span2Node = new Element(Tag.valueOf("span"),"");
        span2Node.attr("data-atex-af","$ID/PoynterOSTextThreeL Roman");
        span2Node.attr("data-atex-fs", "Normal");
        span2Node.attr("data-atex-cfill", "Color/Black");

        org.jsoup.nodes.Node nestedSpan1Node = new Element(Tag.valueOf("span"),"");
        nestedSpan1Node.attr("class", "x-atex-note");
        nestedSpan1Node.attr("x-atex-user", "");
        nestedSpan1Node.attr("x-atex-created", "");
        nestedSpan1Node.attr("x-atex-modified", "");
        ((Element) nestedSpan1Node).text("Embedded content ");

        ((Element) span2Node).appendChild(nestedSpan1Node);

        org.jsoup.nodes.Node span3Node = new Element(Tag.valueOf("span"),"");
        span3Node.attr("data-atex-cstyle", "EMBED"+index);
        span3Node.attr("data-atex-af", "$ID/TitlingGothicFB Normal Black");
        span3Node.attr("data-atex-fs", "Black");
        span3Node.attr("data-atex-cfill", "Color/Black");


        org.jsoup.nodes.Node span4Node = new Element(Tag.valueOf("span"),"");
        span4Node.attr("ata-atex-uat","{ }");
        span4Node.attr("ata-atex-id","cmd");
        span4Node.attr("ata-atex-name","EI");
        span4Node.attr("ata-atex-v1",index);
        span4Node.attr("ata-atex-cstyle","EMBED"+index);
        ((Element) span4Node).text(Entities.getCharacterByName("nbsp").toString());

        org.jsoup.nodes.Node span5Node = new Element(Tag.valueOf("span"),"");
        span5Node.attr("ata-atex-cstyle","EMBED"+index);
        span5Node.attr("ata-atex-af","$ID/TitlingGothicFB Normal Black");
        span5Node.attr("data-atex-fs", "Black");
        span5Node.attr("data-atex-cfill", "Color/Black");

        currentElement.after(span5Node);
        currentElement.after(span4Node);
        currentElement.after(span3Node);
        currentElement.after(span2Node);
        currentElement.after(span1Node);
        return currentElement;
    }

}
