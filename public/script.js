

function MainScript () {};

var STATUS_OK = 200;
var simplifications = new Map();
var complexSentences = new Map();
var editorMode = "simplifyAllMode";
const EDITOR_OFFSET = 255; 
const EDITOR_HEIGHT = "300px"; 
const SYNONYM_SOURCE_SEPARATOR = "-D-";
const NOMINAL_START = "Use verb form: ";

var input = null;
//var originalAvgFreq = 0;
//var mOriginalWC = 0;
//var mOriginalWF = 0;

var originalPosNC = 0;
var originalPosVC = 0;
var revisedPosNC = 0;
var revisedPosVC = 0;

/**
 * Returns list of all complex words
 *
 * @return list of complex words
 */

MainScript.getComplexWords = function() {
	return Array.from(simplifications.keys());
}

MainScript.getComplexSentences = function() {
	return Array.from(complexSentences.keys());
}

/*
 * Matches capitalization of simplifications to complex word
 * (Matches capitalization of first letter only.)
 * 
 * @param word complex word whose capitalization simplifications should match
 * @param simplificationsList list of simplifications for word
 * @return list of simplifications with matching capitalization 
 */
function matchCapitalization(word, simplificationList) {
	//if the complex word isn't empty
	if(word) { 
		//Match capitalization of simplifications according to word
		if(word.charAt(0) === word.charAt(0).toUpperCase()){
		simplificationList = simplificationList.map(function capitalize(simplification) {
			return simplification.charAt(0).toUpperCase() + simplification.slice(1);
		});
		}
		if(word === word.toUpperCase()){
			simplificationList = simplificationList.map(function capitalize(simplification) {
				return simplification.toUpperCase();
			});
		}
	}
	return simplificationList;
}

/**
 * Called when user clicks in the text box. If the user clicks a 
 * complex word, display menu of simplifications.
 * 
 * @param e click event
 */
function onClick (e) {
	deleteExistingDropdown();

	//if the user clicked on a highlighted word, show simplifications menu
	 if(e.target.className === "cm-complexStyle") { 
		
		//get the token at the location clicked
		var coords = {left: e.pageX, top: e.pageY};
		var loc = editor.coordsChar(coords);
		var token = editor.getTokenAt(loc, true);
		document.getElementById("infoBox").innerHTML = "";

		// if the user clicked within a definable token, add a dropdown menu 
		if(loc.ch !== token.end) {
			createDropdown({line: loc.line, ch: token.start}, {line: loc.line, ch: token.end}, token.string);
    	}
	}else if(e.target.className.startsWith("cm-complexSentenceStyle")) {
		// get the sentence info (a bit hacky)
		var sentenceInfo = e.target.className.substring("cm-complexSentenceStyle".length);
		
		// remove the "cm-" tags
		sentenceInfo = sentenceInfo.replace(/ cm\-/g, " ");
		
		// remove leading space
		sentenceInfo = sentenceInfo.substring(1);
		
		//console.log("clicked: " + sentenceInfo);
		//console.log("sentence info: " + complexSentences.get(sentenceInfo));
		
		//get the token at the location clicked
		var coords = {left: e.pageX, top: e.pageY};
		var loc = editor.coordsChar(coords);
		var token = editor.getTokenAt(loc, true);

		// if the user clicked within a definable token, add a dropdown menu 
		if(loc.ch !== token.end) {
			document.getElementById("infoBox").innerHTML = complexSentences.get(sentenceInfo);
    	}
	}
}

/**
 * Deletes any existing dropdown menu
 */
function deleteExistingDropdown() {
	if ((existingMenu = document.getElementById("dropId")) !== null) {
		existingMenu.parentNode.removeChild(existingMenu);
	}
}

/**
 * Gets original text stats, highlights complex words and gets their simplifications
 *
 */
function simplify() {
	reset();
	input = getInputText();
		
	//if there is some input, get simplifications
	if(input !== "") {
		//document.getElementById("loader").style.display = "block";
		$('.loading').show(0);
		var wordnetChecked = document.getElementById("wordnetCheck").checked;
		var UMLSChecked = document.getElementById("UMLSCheck").checked;
		var negationChecked = document.getElementById("negationCheck").checked;
		var affixChecked = document.getElementById("affixCheck").checked;
		var nominalChecked = document.getElementById("nominalCheck").checked;
		var sliderValue = document.getElementById("freqAdjustSlider").value;
		// display original text stats
		getStatistics("original", input).then(function() {
			// send request to simplify input
			sendSimplifyRequest(input, wordnetChecked, UMLSChecked, negationChecked, affixChecked, nominalChecked, sliderValue);
			var moriginalWF = parseInt(document.getElementsByClassName("original-wf")[0].innerHTML);
			var moriginalWC = parseInt(document.getElementsByClassName("original-wc")[0].innerHTML);
			var mrevisedWF = parseInt(document.getElementsByClassName("revised-wf")[0].innerHTML);
			var mrevisedWC = parseInt(document.getElementsByClassName("revised-wc")[0].innerHTML);
			sendAnalyticsRequest(input, wordnetChecked, UMLSChecked, negationChecked, affixChecked, nominalChecked, moriginalWC, moriginalWF, "-1" , "-1", "Simplify");
		}).catch(function(err) {
			console.log("Error: " + err);

			// display error in stats box
			document.getElementsByClassName("original-wc")[0].innerHTML = "Error: could not get statistics";
			document.getElementsByClassName("original-wf")[0].innerHTML = "Error: could not get statistics";

			// try to simplify the text anyway
			sendSimplifyRequest(input);
		})
	}
}
/**
 * Gets simplification data from server, highlights complex words and stores simplifications
 */
function sendSimplifyRequest(input, wordnetChecked, UMLSChecked, negationChecked, affixChecked, nominalChecked, sliderValue) {
	$.ajax({
		type: "POST",
		url: "/simplify",
		data: {value: input, wordnetChecked: wordnetChecked, UMLSChecked:UMLSChecked, 
			negationChecked:negationChecked, affixChecked:affixChecked, nominalChecked:nominalChecked, sliderValue:sliderValue},
		success: function(data) {
			var i;
			//document.getElementById("loader").style.display = "none";
			$('.loading').hide(0);
			editor.setValue(input);

			//add complex words and simplifications to simplifications map
			for(i = 0; i < data.length; i++) {
				if( data[i].isLexicalChange ){
					// shouldn't need to lowercase the word, but just in case
					simplifications.set(data[i].word.toLowerCase(), data[i].simplifications);
				}else{
					// this is a sentence annotation
					complexSentences.set(data[i].word, data[i].simplifications[0]);
				}
			}
			
			// highlight complex words
			//editor.setOption("mode", "simplifyMode");
			editor.setOption("mode", editorMode);
			
			// Add event listener for when user clicks on complex words
			editor.getWrapperElement().addEventListener("click", onClick);

			// Enable get stats button 
			// var statsBtn = document.getElementById("statsBtn");
			// statsBtn.removeAttribute("disabled");
			//statsBtn.className = ("statistics");
			
		},
		error: function(x,t,m) {
			console.log("Error:\nx: " + x + "\nt: " + t + "\nm:" + m);
			$('.loading').show(0);
			//document.getElementById("loader").style.display = "none";
			alert("Error message:  Could not simplify this input");
			//editor.setValue("Error: Could not simplify this input.");
		}
	});
}
	
function sendAnalyticsRequest(input, wordnetChecked, UMLSChecked, negationChecked, affixChecked, nominalChecked, originalWC, originalWF, revisedWC, revisedWF, buttonClicked) {
	$.ajax({
		type: "POST",
		url: "/analytics",
		data: {value: input, wordnetChecked: wordnetChecked, UMLSChecked:UMLSChecked, 
			negationChecked:negationChecked, affixChecked:affixChecked, nominalChecked:nominalChecked, originalWC:originalWC,
			originalWF:originalWF, revisedWC:revisedWC, revisedWF:revisedWF, buttonClicked:buttonClicked},
		success: function(data) {
			var i;
			document.getElementsByClassName("info1")[0].innerHTML="<b>ID:</b> " + data.sessionID + "<br>";
			document.getElementsByClassName("info2")[0].innerHTML="<b>Text ID</b>: " + data.textID;
		}
	});
}

function replaceTextAnalyticsRequest(originalWord, replacedWord, sourceOfWord, optionsOfWord) {
	$.ajax({
		type: "POST",
		url: "/replacetext",
		data: {originalWord:originalWord, replacedWord:replacedWord, sourceOfWord:sourceOfWord, optionsOfWord:optionsOfWord},
		success: function(data) {
			var i;
		}
	});
}

/**
 * Creates a menu of simplifications for a complex word
 * 
 * @param word the complex word to create simplifications menu for
 * @param start_location top left startpoint of menu (bottom left of word)
 * @param end_location bottom right end of word
 */
function createDropdown(start_location, end_location, word) {
	var dropdownDiv;
	var contentDiv;
	var menu_items = [];
	var menu_item;
	var i;
	var o_word = word;
	//var wordOptions = [];
	//console.log("Creating menu")
	//console.log(start_location)

	// create the dropdown div
	dropdownDiv = document.createElement("div");
	dropdownDiv.className += "dropdown";
	dropdownDiv.id = "dropId";
	
	// create child dropdown content div
	contentDiv = document.createElement("div");
	contentDiv.className += "dropdown-content";
	dropdownDiv.appendChild(contentDiv);
	
	// get list of simplifications for word and add to the menu
	menu_items = matchCapitalization(word, simplifications.get(word.toLowerCase()).slice(0));
	for(i = 0; i < menu_items.length; i++) {
		// figure out the source
		var temp = menu_items[i].split(SYNONYM_SOURCE_SEPARATOR);
		
		// SHOULD PROBABLY CHECK THAT IT HAS LENGTH 2, BUT WE'LL IGNORE FOR NOW
		var word = temp[0];
		var source = temp[1];
				
		if( source.toLowerCase() == "wordnet" ){
			menu_item = document.createElement("wordnet");
		}else if( source.toLowerCase() == "umls" ){
			menu_item = document.createElement("umls");
		}else if( source.toLowerCase() == "negation" ){
			menu_item = document.createElement("negation");
		}else{
			menu_item = document.createElement("generic");
		}
		
		menu_item.innerHTML = word;
		contentDiv.appendChild(menu_item);
		
		//replace original word with this simplification when clicked
		menu_item.addEventListener("click", function() {
			if( this.innerHTML.startsWith(NOMINAL_START) ){
				editor.replaceRange(this.innerHTML.substring(NOMINAL_START.length), start_location, end_location);
			}else{
				editor.replaceRange(this.innerHTML, start_location, end_location);
			}
			
			var wordOptions = [];
			for(j = 0; j < menu_items.length; j++) {
				var temp1 = menu_items[j].split(SYNONYM_SOURCE_SEPARATOR);
				wordOptions.push(temp1[0]);
			}
			var test1 = wordOptions.join();			
			replaceTextAnalyticsRequest(o_word, this.innerHTML, source, test1);
		});
	}
	
	// add menu widget beneath complex word
	var menu  = editor.addWidget(start_location, dropdownDiv, true);

	// find location of right side of menu
	var menu_left = editor.cursorCoords(start_location).left;
	var menu_width = document.getElementsByClassName("dropdown-content")[0].offsetWidth;
	var menu_right = menu_left + menu_width;

	// find location of right side of window
	var editor_right = window.innerWidth - EDITOR_OFFSET;

	// if menu goes off the right of the editor, move it left so it ends at right of word
	if(menu_right > editor_right) {
		var word_right = editor.cursorCoords(end_location).left;
		var menu_top = editor.cursorCoords(start_location).top;
		deleteExistingDropdown();
		menu = editor.addWidget(editor.coordsChar({left: (editor_right - menu_width), top: menu_top}), dropdownDiv, true);
	}
}

//create table	
function createTable(original,revised) {
	  
	  var table = document.createElement("table");
	  table.className="gridtable table table-bordered mt-4";
	  
	  var tbody= document.createElement("tbody"); 
	  var tr = document.createElement("tr");

	  table.appendChild(tbody);

	  //create header
	  tbody.appendChild(tr);

	  var heading = ["Metrics" , "Original", "Simplified"];
	  var cols = ["Number of Sentences" , "Average Sentence Length (Words/Sentence)", "Word Frequency" , "Word Count", "Noun Count", "Verb Count", "Text"];
	  var colTitles = ["Total number of Sentences in the text" , "Total number of Words per sentence)", "A higher frequency indicates you used simpler words" , "Number of Words in the text", "Replacing the nouns with verbs where possible as a lower noun count is better", "Replacing verbs with nouns where possible as a higher verb count is better", "Original and Revised Text"];

	    for (var col = 0; col<heading.length; col++)
	    {
			var th = document.createElement("th");
			//th.width = '75';
			th.style.backgroundColor = "#88D0DA";
			th.appendChild(document.createTextNode(heading[col]));
			tr.appendChild(th);
	    }
       
		for (var f=0; f<original.length; f++)
			{
				var tr = document.createElement("tr"); 
				var td1 = document.createElement("th");
				td1.setAttribute('title', colTitles[f]);
								
				//td1.className = "rowTitle";
				var td2 = document.createElement("td");
				var td3 = document.createElement("td");
					td1.appendChild(document.createTextNode(cols[f]));
					td2.appendChild(document.createTextNode(original[f]));
					td3.appendChild(document.createTextNode(revised[f]));
					tr.appendChild(td1);
					tr.appendChild(td2);
					tr.appendChild(td3);
					tbody.appendChild(tr);
			}
	  	return table;
	}

function drawTable(original,revised) {
	document.getElementById("stats").appendChild(createTable(original,revised));
	// var x = window.open("table.html");
	// //if(x.addEventListener){
    // x.addEventListener("DOMContentLoaded",function(){
    // x.document.body.appendChild(createTable(original,revised));
    // });//}
	/*else{
		x.attachEvent("DOMContentLoaded",function(){
	    	x.document.body.appendChild(createTable(original,revised));
	    });
	}*/
}

/**
 * Resets everything related to old complex words
 *
 */
function reset() {
	//clear old complex word data
	simplifications.clear();
	
	// clear old sentence tagging
	complexSentences.clear();
	
	//reset to null highlighting mode
	editor.setOption("mode", "null");
	
	//delete dropdown menu if there is one 
	deleteExistingDropdown();
	
	//remove event listener that checks for clicks on highlighted words
	editor.getWrapperElement().removeEventListener("click", onClick);
	
	//disable get stats button
	// var statsBtn = document.getElementById("statsBtn");
	// statsBtn.setAttribute("disabled", true);
	//statsBtn.className = ("statistics disabled");
	
	//clear all fields in the statistics box
	document.getElementsByClassName("original-wc")[0].innerHTML = "";
	document.getElementsByClassName("original-wf")[0].innerHTML = "";
	document.getElementsByClassName("revised-wc")[0].innerHTML = "";
	document.getElementsByClassName("revised-wf")[0].innerHTML = "";
	document.getElementsByClassName("info1")[0].innerHTML = "<b>ID: </b><br>";
	document.getElementsByClassName("info2")[0].innerHTML = "<b>Text ID: </b>";
	
	// clear the sentence simplification box
	document.getElementById("infoBox").innerHTML = "";
}

/**
 * Clears the text box
 */
document.getElementById("clearBtn").onclick = function clear() {
	editor.setValue("");
	reset();
}

/**
 * Gets input text from the text box 
 */
function getInputText() {
	return editor.getValue();
}

function getRandomColor(limit) {
	var color = [];
	for(var i=0;i<limit;i++){
		if(i%2==0){
			var colorCode = "hsl(" + Math.floor(i * (360 / limit) % 360) + ",80%, 50%)";;
			color.push(colorCode);
		} else {
			var colorCode = "hsl(" + Math.floor(Math.random() * 360) + ", 100%, 75%)";
			color.push(colorCode);
		}
		
	}
	return color;
}

function highlight(text, styles, dontwraparound, saferCharCode){
		
	if (styles && (styles instanceof Array || styles.isArray)){
		styles = {
			'background:yellow': styles
		}
	}
	var arr = saferCharCode ? Array.from(text) : [].slice.call(text),
		arrlen = arr.length,
		cur,
		value_start,
		value_end,
		curstyle,
		i,
		k;
	if (dontwraparound){
		for (k in styles){
			cur = styles[k], i=cur.length, curstyle = '<span style="' + k + '">';
			while (i--)
				arr[cur[i][0]] = curstyle + arr[cur[i][0]], arr[cur[i][1]] += '</span>';
		}
	} else {
		for (k in styles){
			cur = styles[k], i=cur.length, curstyle = '<span style="' + k + '">';
			while (i--){
				value_start = (arrlen + (cur[i][0] % arrlen)) % arrlen;
				value_end = (arrlen + (cur[i][1] % arrlen)) % arrlen;
				if (value_start <= value_end){
					arr[value_start] = curstyle + arr[value_start];
					arr[value_end] += '</span>';
				} else {
					arr[value_end] = curstyle + arr[value_end];
					arr[value_start] += '</span>';
				}
			}
		}
	}
	return arr.join('');
};

function processChains(text, data, colorsArray){
	var inputText = document.getElementById("textArea1");
	var globalArray = [];
	var backgroundStr = [];
	var globalObj = {};
	var localArray = [];
	for(var i=0; i<data.length-5;i++){
		backgroundStr.push('background:'+colorsArray[i]);
		var chain = data[i].Chain;
		for(var j=0;j<chain.length;j++){
			localArray.push(chain[j].location);
		}
		globalArray.push(localArray);
		localArray=[];
	}
	
	for(var k=0;k<globalArray.length;k++){
		globalObj[backgroundStr[k]] = globalArray[k];
	}

	inputText.innerHTML = highlight(text, globalObj , false , true);
}

function getExactLexicalChains(){
	var inputValue = getInputText();
	$('#statisticsBox').hide();
	$('#radioBoxes').show();
	$('#wordsBox').hide();
	document.getElementById('lexChain').innerHTML = '0';
	document.getElementById('avgChainLen').innerHTML = '0';
	document.getElementById('avgChainCount').innerHTML = '0';
	document.getElementById('crossChain').innerHTML = '0';
	document.getElementById('hdlChain').innerHTML = '0';
	if(inputValue === ""){
		$("#infoMsg1").show();
		$("#textArea1").hide();
	} else {
		$("#infoMsg1").hide();
		$("#textArea1").show();
		$("#textArea1").text(getInputText());
		$('.loading').show(0);
		$.ajax({
			type: "POST",
			url: "/exactLexicalChains",
			data: {value: getInputText()},
			success: function(data) {
				$('.loading').hide(0);
				var data = JSON.parse(data);
				var colorsArray = [];
				colorsArray = getRandomColor(data.length);
				processChains(inputValue, data, colorsArray);
				for(var i=data.length-1;i>=data.length-5;i--){
					if(data[i].hasOwnProperty("Number of Lexical Chains")){
						document.getElementById('lexChain').innerHTML = data[i]["Number of Lexical Chains"];
					}
					if(data[i].hasOwnProperty("Average Chain Length")){
						document.getElementById('avgChainLen').innerHTML = data[i]["Average Chain Length"];
					}
					if(data[i].hasOwnProperty("Average Chain Span")){
						document.getElementById('avgChainCount').innerHTML = data[i]["Average Chain Span"];
					}
					if(data[i].hasOwnProperty("Number of Cross Chains")){
						document.getElementById('crossChain').innerHTML = data[i]["Number of Cross Chains"];
					}
					if(data[i].hasOwnProperty("Number of Half-Document-Length Chains")){
						document.getElementById('hdlChain').innerHTML = data[i]["Number of Half-Document-Length Chains"];
					}
				}			
			},
			error:function(err){
				$('#infoMsg1').show();
			}
		});
	}
}

function getSynonymousLexicalChains(){
	var inputValue = getInputText();
	$('#statisticsBox').hide();
	$('#radioBoxes').show();
	$('#wordsBox').hide();
	if(inputValue === ""){
		$("#infoMsg1").show();
		$("#textArea1").hide();
	} else {
		$("#infoMsg1").hide();
		$("#textArea1").show();
		$("#textArea1").text(getInputText());
		$('.loading').show(0);
		$.ajax({
			type: "POST",
			url: "/synonymousLexicalChains",
			data: {value: getInputText()},
			success: function(data) {
				$('.loading').hide(0);
				var data = JSON.parse(data);
				var colorsArray = [];
				colorsArray = getRandomColor(data.length);
				processChains(inputValue, data, colorsArray);
				for(var i=data.length-1;i>=data.length-5;i--){
					if(data[i].hasOwnProperty("Number of Lexical Chains")){
						document.getElementById('lexChain').innerHTML = data[i]["Number of Lexical Chains"];
					}
					if(data[i].hasOwnProperty("Average Chain Length")){
						document.getElementById('avgChainLen').innerHTML = data[i]["Average Chain Length"];
					}
					if(data[i].hasOwnProperty("Average Chain Span")){
						document.getElementById('avgChainCount').innerHTML = data[i]["Average Chain Span"];
					}
					if(data[i].hasOwnProperty("Number of Cross Chains")){
						document.getElementById('crossChain').innerHTML = data[i]["Number of Cross Chains"];
					}
					if(data[i].hasOwnProperty("Number of Half-Document-Length Chains")){
						document.getElementById('hdlChain').innerHTML = data[i]["Number of Half-Document-Length Chains"];
					}
				}			
			},
			error:function(err){
				$('#infoMsg1').show();
			}
		});
	}
}

function getSemanticLexicalChains(){
	var inputValue = getInputText();
	$('#statisticsBox').hide();
	$('#radioBoxes').show();
	$('#wordsBox').hide();
	if(inputValue === ""){
		$("#infoMsg1").show();
		$("#textArea1").hide();
	} else {
		$("#infoMsg1").hide();
		$("#textArea1").show();
		$("#textArea1").text(getInputText());
		$('.loading').show(0);
		$.ajax({
			type: "POST",
			url: "/semanticLexicalChains",
			data: {value: getInputText()},
			success: function(data) {
				$('.loading').hide(0);
				var data = JSON.parse(data);
				var colorsArray = [];
				colorsArray = getRandomColor(data.length);
				processChains(inputValue, data, colorsArray);
				for(var i=data.length-1;i>=data.length-5;i--){
					if(data[i].hasOwnProperty("Number of Lexical Chains")){
						document.getElementById('lexChain').innerHTML = data[i]["Number of Lexical Chains"];
					}
					if(data[i].hasOwnProperty("Average Chain Length")){
						document.getElementById('avgChainLen').innerHTML = data[i]["Average Chain Length"];
					}
					if(data[i].hasOwnProperty("Average Chain Span")){
						document.getElementById('avgChainCount').innerHTML = data[i]["Average Chain Span"];
					}
					if(data[i].hasOwnProperty("Number of Cross Chains")){
						document.getElementById('crossChain').innerHTML = data[i]["Number of Cross Chains"];
					}
					if(data[i].hasOwnProperty("Number of Half-Document-Length Chains")){
						document.getElementById('hdlChain').innerHTML = data[i]["Number of Half-Document-Length Chains"];
					}
				}			
			},
			error:function(err){
				$('#infoMsg1').show();
			}
		});
	}
}

function getStatistics(textIdentifier, input) {
	return new Promise (function(resolve, reject) {
		if (textIdentifier === "original" || textIdentifier === "revised") {
			var wc = document.getElementsByClassName(textIdentifier + "-wc")[0];
			var wf = document.getElementsByClassName(textIdentifier + "-wf")[0];
			
			// show that stats are being calculated
			wc.innerHTML = wf.innerHTML = "calculating..."
				
			// get stats from the server
			$.post("/statistics", {
				value: input
			}, function(data){
				var stats=[input,getInputText(),]
				wc.innerHTML = data.numWords.toLocaleString(); // display word count
				wf.innerHTML = (Math.round(data.averageFrequency)).toLocaleString(); // display avg word freq
				
				// if getting statistics for revised text, get results and display
				if(textIdentifier === "revised") { 
					var originalWF = parseInt(document.getElementsByClassName("original-wf")[0].innerHTML);
					//var percent = Math.round((parseInt(wf.innerHTML)/originalWF -1)*100);
					var percent = ((parseFloat(wf.innerHTML)/originalWF -1)*100).toFixed(2);
					revisedPosNC = data.nouns.toLocaleString();
					revisedPosVC = data.verbs.toLocaleString();
				}
				else {
					originalPosNC = data.nouns.toLocaleString();
					originalPosVC = data.verbs.toLocaleString();
				}

				resolve();	//done
			})
			.fail(function(err){
				reject();
				throw err;
			});
		} else {
			resolve(); // do nothing
		}
	});
	
}

/** 
 * Adds event listeners to the page 
 */
function addListeners() {	
	// delete any existing dropdown on window resize
	window.addEventListener("resize", deleteExistingDropdown);
}

function defaultTab(){
	$('#statisticsBox').show();
	$('#wordsBox').hide();
	$('#customRadio1').prop("checked", true);
	$('#radioBoxes').hide();
}

function getRevisedStats(){
	var rwordnetChecked = document.getElementById("wordnetCheck").checked;
	var rUMLSChecked = document.getElementById("UMLSCheck").checked;
	var rnegationChecked = document.getElementById("negationCheck").checked;
	var raffixChecked = document.getElementById("affixCheck").checked;
	var rnominalChecked = document.getElementById("affixCheck").checked;
	var rinput = getInputText();
	var original=[];
	var revised=[];
	$('#statisticsBox').hide();
	$('#radioBoxes').hide();
	$('#wordsBox').show();
	$( ".gridtable" ).remove();
	if(rinput === ""){
		$("#infoMsg").show();
	} else {
		$('.loading').show(0);
		$("#infoMsg").hide();
		getStatistics("revised", getInputText()).then(function() {
			$('.loading').hide(0);
			var roriginalWF = parseInt((document.getElementsByClassName("original-wf")[0].innerHTML).replace(/,/g,"")); //remove formatting
			var roriginalWC = parseInt((document.getElementsByClassName("original-wc")[0].innerHTML).replace(/,/g,""));
			var rrevisedWF = parseInt((document.getElementsByClassName("revised-wf")[0].innerHTML).replace(/,/g,""));
			var rrevisedWC = parseInt((document.getElementsByClassName("revised-wc")[0].innerHTML).replace(/,/g,""));
			var roriginalNS =  input.split(".").length-1;  // number of sentences
			var rrevisedNS =  rinput.split(".").length-1;
			var roriginalAvgWords = Math.round(roriginalWC/roriginalNS);  // avg words per sentence
			var rrevisedAvgWords = Math.round(rrevisedWC/rrevisedNS);
			sendAnalyticsRequest(rinput, rwordnetChecked, rUMLSChecked, rnegationChecked, raffixChecked, rnominalChecked, roriginalWC, roriginalWF, rrevisedWC, rrevisedWF, "Get Stats");
			original=[roriginalNS.toLocaleString(),roriginalAvgWords.toLocaleString(),roriginalWF.toLocaleString(),roriginalWC.toLocaleString(), originalPosNC, originalPosVC, input];
			revised=[rrevisedNS.toLocaleString(),rrevisedAvgWords.toLocaleString(),rrevisedWF.toLocaleString(),rrevisedWC.toLocaleString(), revisedPosNC, revisedPosVC,rinput];
			drawTable(original,revised);
		}).catch(function(err){
			console.log("Error: " + err);
			$('#infoMsg').show();
			//$('.loading').show(0);
			// display error messages in stats box
			document.getElementsByClassName("revised-wc")[0].innerHTML = "Error: could not get statistics.";
			document.getElementsByClassName("revised-wf")[0].innerHTML = "Error: could not get statistics.";
		});
	}
}

$('.loading').hide(0);

// $(window).on('resize', function() {
//     if($(window).width() < 600) {
// 		const EDITOR_HEIGHT = "200px"; 
// 		editor.setSize(null, EDITOR_HEIGHT); 
// 	} else if($(window).width() >= 600 && $(window).width() <= 1024) {
// 		const EDITOR_HEIGHT = "400px"; 
// 		editor.setSize(null, EDITOR_HEIGHT);
//     } else if($(window).width() > 1024 && $(window).width() <= 2000) {
// 		const EDITOR_HEIGHT = "600px"; 
// 		editor.setSize(null, EDITOR_HEIGHT);
//     } else {
// 		const EDITOR_HEIGHT = "800px"; 
// 		editor.setSize(null, EDITOR_HEIGHT);
// 	}
// });

// create text editor
var editor = CodeMirror(document.getElementById("codeMirrorBox"), {
  placeholder: "Enter text to be simplified here",
  mode:  null,
  lineWrapping: true
});
//editor.setSize(null, EDITOR_HEIGHT);


// insert editor before stats box so they are arranged correctly on page
//var boxes = document.getElementsByClassName("boxes")[0];
//var editorElt = document.getElementsByClassName("CodeMirror")[0];
//var statsBox = document.getElementsByClassName("box")[0];
//boxes.insertBefore(editorElt, statsBox);

addListeners();

// setup slider
document.getElementById("freqAdjustSlider").oninput = function() {
  document.getElementById("freqLevel").innerHTML = this.value;
}


// throw any errors that promises would otherwise silently get rid of
/*Promise.onPossiblyUnhandledRejection(function(err){
	console.log("Unhandled Error: " + err);
	throw err;	
});*/
