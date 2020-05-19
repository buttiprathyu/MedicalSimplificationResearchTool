package edu.pomona.simplification.lexicalChainsAnnotator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

//import edu.pomona.simplification.Preferences;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * An class showing how you would call the LexicalChainsDifficultyAnntotator
 * class.  When I use it, this part is already handled, but I wanted to show
 * you to help with debugging, etc. and to get a feeling for how it would
 * be called.
 * 
 * @author drk04747
 *
 */
public class LexicalChainCall {

	private StanfordCoreNLP pipeline;
	private LexicalChainsAnnotator lexChainsAnnotator = new LexicalChainsAnnotator();

	public LexicalChainCall() {
		// setup the CoreNLP pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,depparse");
		//props.setProperty("threads", Integer.toString(Preferences.NUM_PARSER_THREADS));
		pipeline = new StanfordCoreNLP(props);
	}

	public void processFiles(String filename) {
		String text = readFile(filename);
		Annotation annotator = new Annotation(text);
		pipeline.annotate(annotator);

		// create a CoreDocument for ease of accessing annotations
		CoreDocument document = new CoreDocument(annotator);
		List<CoreSentence> sentences = document.sentences();

		// get the lexical chains annotations
		lexChainsAnnotator.sentenceDifficulty(sentences);
	}

	public void processText(String text) {
		Annotation annotator = new Annotation(text);
		pipeline.annotate(annotator);

		// create a CoreDocument for ease of accessing annotations
		CoreDocument document = new CoreDocument(annotator);
		List<CoreSentence> sentences = document.sentences();

		// get the lexical chains annotations
		lexChainsAnnotator.sentenceDifficulty(sentences);

	}
	public String processTextToChains(Enum type, String text) {
		Annotation annotator = new Annotation(text);
		pipeline.annotate(annotator);

		// create a CoreDocument for ease of accessing annotations
		CoreDocument document = new CoreDocument(annotator);
		List<CoreSentence> sentences = document.sentences();

		// get the lexical chains annotations
		return lexChainsAnnotator.getChainsByJSON(type, sentences);
	}

	private String readFile(String filename) {
		StringBuffer buffer = new StringBuffer();

		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));

			String line;

			while((line = in.readLine()) != null) {
				buffer.append(line + " ");
			}

			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buffer.toString();
	}

	public static void main(String[] args) {
		LexicalChainCall caller = new LexicalChainCall();

		// process a file
		// caller.processFiles(filename);

		// or process a text
		// caller.processTexts(someText);
//		String text1 = "Local symptoms may occur due to the mass of the tumor or its ulceration. "
//				+ "For example, mass effects from lung cancer can block the bronchus resulting in cough "
//				+ "or pneumonia; esophageal cancer can cause narrowing of the esophagus, making it difficult "
//				+ "or painful to swallow; and colorectal cancer may lead to narrowing or blockages in the "
//				+ "bowel, affecting bowel habits. Masses in breasts or testicles may produce observable lumps. "
//				+ "Ulceration can cause bleeding that, if it occurs in the lung, will lead to coughing up blood, "
//				+ "in the bowels to anemia or rectal bleeding, in the bladder to blood in the urine and in the "
//				+ "uterus to vaginal bleeding. Although localized pain may occur in advanced cancer, the initial "
//				+ "swelling is usually painless. Some cancers can cause a buildup of fluid within the chest or abdomen.";
//		System.out.println("Text One: \n" + text1);
//		System.out.println(caller.processTextToChains(LexicalChainType.Synonymous, text1));
//		System.out.println("++++++++++++++++++++++++++");
//		String text2 = "Aarskog-Scott syndrome is a rare disease inherited as autoso- mal dominant or x-linked "
//				+ "and characterized by short stature, facial abnormalities, skeletal and genital anomalies. "
//				+ "The Aarskog-Scott syndrome is also known as the Aarskog syndrome and faciogenital dysplasia. "
//				+ "Aaarskog-Scott syndrome is due to mutation in the fgd1 gene. Fgd1 encodes a guanine nucleotide "
//				+ "exchange factor(gef) that specifically activates cdc42, a member of rho (rashomology) family of p21 gtpases.";
//		System.out.println("Text Two: \n" + text2);
//		System.out.println(caller.processTextToChains(LexicalChainType.Synonymous, text2));
//		System.out.println("++++++++++++++++++++++++++");
//		String text3 = "In addition to the ultrasound or afp scanning, it is also necessary for children with this disease to be "
//				+ "checked for other birth defects because genetic disorders are usually associated with some of the abdominal wall "
//				+ "defects.";
//		System.out.println(caller.processTextToChains(LexicalChainType.Semantic, text3));
//		String text4 = "Aagenaes syndrome Aagenaes syndrome is a syndrome characterised by congenital hypoplasia of lymph vessels, which causes lymphedema of the legs and recurrent cholestasis in infancy, and slow progress to hepatic cirrhosis and giant cell hepatitis with fibrosis of the portal tracts.The genetic cause is unknown, but it is autosomal recessively inherited and the gene is located to chromosome 15q1,2. A common feature of the condition is a generalised lymphatic anomaly, which may be indicative of the defect being lymphangiogenetic in origin1. The condition is particularly frequent in southern Norway, where more than half the cases are reported from, but is found in patients in other parts of Europe and the U.S..It is named after Oystein Aagenaes, a Norwegian paediatrician. It is also called cholestasis-lymphedema syndrome (CLS).";
//		System.out.println(caller.processTextToChains(LexicalChainType.Exact, text4));
//		String text5 = "Adrenocortical carcinoma Adrenocortical carcinoma, also adrenal cortical carcinoma (ACC) and adrenal cortex cancer, is an aggressive cancer originating in the cortex (steroid hormone-producing tissue) of the adrenal gland. Adrenocortical carcinoma is a rare tumor, with incidence of 1–2 per million population annually.Adrenocortical carcinoma has a bimodal distribution by age, with cases clustering in children under 5, and in adults 30–40 years old.Adrenocortical carcinoma is remarkable for the many hormonal syndromes which can occur in patients with steroid hormone-producing (\"functional\") tumors, including Cushing's syndrome, Conn syndrome, virilization, and feminization. Adrenocortical carcinoma has often invaded nearby tissues or metastasized to distant organs at the time of diagnosis, and the overall 5-year survival rate is only 20–35%.The widely used angiotensin-II-responsive steroid-producing cell line H295R was originally isolated from a tumor diagnosed as adrenocortical carcinoma.  Adrenocortical carcinoma may present differently in children and adults. Most tumors in children are functional, and virilization is by far the most common presenting symptom, followed by Cushing's syndrome and precocious puberty.Among adults presenting with hormonal syndromes, Cushing's syndrome alone is most common, followed by mixed Cushing's and virilization (glucocorticoid and androgen overproduction). Feminization and Conn syndrome (mineralocorticoid excess) occur in less than 10% of cases. Rarely, pheochromocytoma-like hypersecretion of catecholamines has been reported in adrenocortical cancers.Non-functional tumors (about 40%, authorities vary) usually present with abdominal or flank pain, varicocele and renal vein thrombosisor they may be asymptomatic and detected incidentally.All patients with suspected adrenocortical carcinoma should be carefully evaluated for signs and symptoms of hormonal syndromes. For Cushing's syndrome (glucocorticoid excess) these include weight gain, muscle wasting, purple lines on the abdomen, a fatty \"buffalo hump\" on the neck, a \"moonlike\" face, and thinning, fragile skin. Virilism (androgen excess) is most obvious in women, and may produce excess facial and body hair, acne, enlargement of the clitoris, deepening of the voice, coarsening of facial features, cessation of menstruation. Conn syndrome (mineralcorticoid excess) is marked by high blood pressure which can result in headache and hypokalemia (low serum potassium, which can in turn produce muscle weakness, confusion, and palpitations) low plasma renin activity, and high serum aldosterone. Feminization (estrogen excess) is most readily noted in men, and includes breast enlargement, decreased libido and impotence.The main etiologic factor of adrenocortical cancer is unknown. Families with Li–Fraumeni syndrome have increased risk. The p53, retinoblastoma protein (RB) tumor suppressor genes located on chromosomes 17p, 13q respectively, may be changed. The genes h19, insulin-like growth factor II (IGF-II), p57kip2 are important for fetal growth and development. They are located on chromosome 11p. Expression of the h19 gene is markedly reduced in both nonfunctioning and functioning adrenal cortical carcinomas, especially in tumors producing cortisol and aldosterone. There is also a loss of activity of the p57kip2 gene product in virilizing adenomas and adrenal cortical carcinomas. In contrast, IGF-II gene expression has been shown to be high in adrenal cortical carcinomas. Finally, c-myc gene expression is relatively high in neoplasms, and it is often linked to poor prognosis.Hormonal syndromes should be confirmed with laboratory testing. Laboratory findings in Cushing syndrome include increased serum glucose (blood sugar) and increased urine cortisol. Adrenal virilism is confirmed by the finding of an excess of serum androstenedione and dehydroepiandrosterone. Findings in Conn syndrome include low serum potassium, low plasma renin activity, and high serum aldosterone. Feminization is confirmed with the finding of excess serum estrogen. Radiological studies of the abdomen, such as CT scans and magnetic resonance imaging are useful for identifying the site of the tumor, differentiating it from other diseases, such as adrenocortical adenoma, and determining the extent of invasion of the tumor into surrounding organs and tissues. CT scans of the chest and bone scans are routinely performed to look for metastases to the lungs and bones respectively. These studies are critical in determining whether or not the tumor can be surgically removed, the only potential cure at this time.Adrenal tumors are often not biopsied prior to surgery, so diagnosis is confirmed on examination of the surgical specimen by a pathologist. Grossly, adrenocortical carcinomas are often large, with a tan-yellow cut surface, and areas of hemorrhage and necrosis. On microscopic examination, the tumor usually displays sheets of atypical cells with some resemblance to the cells of the normal adrenal cortex. The presence of invasion and mitotic activity help differentiate small cancers from adrenocortical adenomas.There are several relatively rare variants of adrenal cortical carcinoma: Differential diagnosis includes:The only curative treatment is complete surgical excision of the tumor, which can be performed even in the case of invasion into large blood vessels, such as the renal vein or inferior vena cava. The 5-year survival rate after successful surgery is 50–60%, but unfortunately, a large percentage of patients are not surgical candidates. Radiation therapy and radiofrequency ablation may be used for palliation in patients who are not surgical candidates.Chemotherapy regimens typically include the drug mitotane, an inhibitor of steroid synthesis which is toxic to cells of the adrenal cortex,as well as standard cytotoxic drugs. A retrospective analysis showed a survival benefit for mitotane in addition to surgery when compared to surgery alone.The two most common regimens are cisplatin, doxorubicin, etoposide + mitotane and streptozotocin + mitotane. It is unknown which regimen is better. Researchers at Uppsala University Hospital initiated a collaboration between adrenocortical cancer specialists in Europe, USA and Australia, to conduct the first ever randomized controlled trial in adrenocortical cancer (FIRM-ACT study), comparing these two regimens. ACC, generally, carries a poor prognosisand is unlike most tumours of the adrenal cortex, which are benign (adenomas) and only occasionally cause Cushing's syndrome. Five-year disease-free survival for a complete resection of a stage I–III ACC is approximately 30%.The most important prognostic factors are age of the patient and stage of the tumor. Poor prognostic factors: mitotic activity, venous invasion, weight of 50g+; diameter of 6.5 cm+, Ki-67/MIB1 labeling index of 4%+, p53+.";
//		System.out.println(caller.processTextToChains(LexicalChainType.Synonymous, text5));

	}
}
