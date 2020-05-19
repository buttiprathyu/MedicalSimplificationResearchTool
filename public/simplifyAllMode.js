/**
 * 
 */

// --------------------
// TODO: REPLACE SPACES IN CLASSTAG HASH KEY SO THAT THEY DON'T ACCIDENTALLY MATCH WITH CSS TAGS

const BEGIN_END_SEPARATOR = "-D-";

CodeMirror.defineMode("simplifyAllMode", function applyStyle () {
	// get all the complex words
	var complexWords = MainScript.getComplexWords();
	var simpleWordMode = {
			token: function(stream,state) {
				var i;

				//get current word (chunk containing only letters and apostrophes) 
				stream.eatWhile(/([a-zA-Z]|\')/); 
				var curWord = stream.current();

				//if current word is a word in quotes, get the word in between 
				while(curWord !== "'" && curWord.charAt(0) === "'" && curWord.charAt(curWord.length - 1) === "'") {
					curWord = curWord.substring(1, curWord.length - 1);
				}

				//if a word matches any complex word, style it with complexStyle
				for(i = 0; i < complexWords.length; i++){
					if (curWord.toLowerCase() == complexWords[i].toLowerCase()) {
						return "complexStyle";
					} 
				}
				//move on to next word until done
				stream.next();  
				return null;
			}
	};

	var complexSentences = MainScript.getComplexSentences(); 
	var sentenceMode =  {
			startState: function() {return {covered: "", complex: "", skipToSentenceEnd: false};},
			token: function(stream,state) {
				if( state.skipToSentenceEnd ){
					stream.eatWhile(/[^\.\?\!]/);
					state.skipToSentenceEnd = false;
				}else if( state.covered === "" ){
					stream.eatSpace();
				
					for(i = 0; i < complexSentences.length; i++) {
						var temp = complexSentences[i].split(BEGIN_END_SEPARATOR);
					
						if( temp.length === 2 ){
							var begin = temp[0];
							var covered = temp[1];
												
							//console.log("Begin search: " + begin + "'");

							// check to see if we've found the beginning of a sentence we're interested in flagging
							if( stream.match(begin) ){
								//console.log("Found: " + begin);
								
								stream.backUp(begin.length);
								
								if( stream.match(covered) ){
									if( !(covered.endsWith(".") ||
										  covered.endsWith("?") ||
										  covered.endsWith("!")) ){
										// it's not a whole sentence match, so we need to skip to the end of the sentence after this match
										state.skipToSentenceEnd = true;
									}
																
									return "complexSentenceStyle " + complexSentences[i];
								}else{
									state.covered = covered;
									state.complex = complexSentences[i];
									stream.eatWhile(/[^ ]/);
									stream.eatSpace();
									return null;
								}
							}							
						}
					}
					
					// if none of the sentence beginning match, skip to the next sentence
					stream.eatWhile(/[^\.\?\!]/)
				}else{
					//console.log("Covered search: " + state.covered);
					// we found the beginning of a sentence, now we're trying to find the covered part
					stream.backUp(1);
					
					// see if it's the end of a sentence
					if( stream.eat(/[\.\?\!]/) ) {
						// couldn't find the covered part, so give up
						state.covered = ""
					}else{
						stream.next()
					
						if( stream.match(state.covered) ){
							// append the class information as well
							state.covered = "";
							state.skipToSentenceEnd = true;
							return "complexSentenceStyle " + state.complex;
						}else{
							stream.eatWhile(/[^ ]/)
						}
					}
				}

				if( !stream.eol() ){
					stream.next();
					stream.eatSpace();
				}

				return null;
			}
	};
	
	
	/*var sentenceMode =  {
			startState: function() {return {covered: "", end: ""};},
			token: function(stream,state) {
				//console.log("Next char: " + stream.peek())
				
				if( state.covered === "" ){
					for(i = 0; i < complexSentences.length; i++) {
						var temp = complexSentences[i].split(BEGIN_END_SEPARATOR);
					
						if( temp.length === 3 ){
							var begin = temp[0];
							var end = temp[1];
							var covered = temp[2];
												
							console.log("Looking for: " + begin + "'");

							// check to see if we've found the beginning of a sentence we're interested in flagging
							if( stream.match(begin) ){
								console.log("Found: " + begin);
								state.covered = covered
								state.end = end
							
							
						
							// find the end of that sentence
							stream.skipTo(end);
							
							// skip the end of the sentence
							for( j = 0; j < end.length; j++ ){
								stream.next();
							}
							
							//stream.eatSpace();
							
							// append the class information as well
							return "complexSentenceStyle " + complexSentences[i];
						}
					}else{
						console.log("Received bad complex sentence information (begin/end): " + complexSentences[i]);
					}
				}

				stream.eatWhile(/[^\.\?\!]/);
				//stream.eatWhile(/[^ ]/);

				if( !stream.eol() ){
					stream.next();
					stream.eatSpace();
				}

				return null;
			}
	};*/
	
	return CodeMirror.overlayMode(sentenceMode, simpleWordMode);
});