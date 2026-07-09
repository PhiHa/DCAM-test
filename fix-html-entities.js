const fs = require('fs');
const path = require('path');

// Comprehensive HTML entity map including Vietnamese characters
const htmlEntities = {
  '&nbsp;': ' ',
  '&amp;': '&',
  '&lt;': '<',
  '&gt;': '>',
  '&quot;': '"',
  '&#39;': "'",
  '&apos;': "'",
  
  // Vietnamese lowercase with acute accent (á)
  '&aacute;': 'á',
  '&eacute;': 'é',
  '&iacute;': 'í',
  '&oacute;': 'ó',
  '&uacute;': 'ú',
  '&yacute;': 'ý',
  
  // Vietnamese uppercase with acute accent (Á)
  '&Aacute;': 'Á',
  '&Eacute;': 'É',
  '&Iacute;': 'Í',
  '&Oacute;': 'Ó',
  '&Uacute;': 'Ú',
  '&Yacute;': 'Ý',
  
  // Vietnamese lowercase with grave accent (à)
  '&agrave;': 'à',
  '&egrave;': 'è',
  '&igrave;': 'ì',
  '&ograve;': 'ò',
  '&ugrave;': 'ù',
  
  // Vietnamese uppercase with grave accent (À)
  '&Agrave;': 'À',
  '&Egrave;': 'È',
  '&Igrave;': 'Ì',
  '&Ograve;': 'Ò',
  '&Ugrave;': 'Ù',
  
  // Vietnamese lowercase with tilde (ã)
  '&atilde;': 'ã',
  '&ntilde;': 'ñ',
  '&otilde;': 'õ',
  
  // Vietnamese uppercase with tilde (Ã)
  '&Atilde;': 'Ã',
  '&Ntilde;': 'Ñ',
  '&Otilde;': 'Õ',
  
  // Vietnamese lowercase with circumflex (â)
  '&acirc;': 'â',
  '&ecirc;': 'ê',
  '&icirc;': 'î',
  '&ocirc;': 'ô',
  '&ucirc;': 'û',
  
  // Vietnamese uppercase with circumflex (Â)
  '&Acirc;': 'Â',
  '&Ecirc;': 'Ê',
  '&Icirc;': 'Î',
  '&Ocirc;': 'Ô',
  '&Ucirc;': 'Û',
  
  // Vietnamese lowercase with horn (ơ, ư)
  '&#417;': 'ơ',
  '&#416;': 'Ơ',
  '&#432;': 'ư',
  '&#431;': 'Ư',
  
  // Vietnamese lowercase with breve (ă)
  '&#259;': 'ă',
  '&#258;': 'Ă',
  
  // Vietnamese d with stroke (đ)
  '&#273;': 'đ',
  '&#272;': 'Đ',
  
  // Additional Vietnamese tone marks on circumflex vowels
  '&#7845;': 'ấ',
  '&#7844;': 'Ấ',
  '&#7847;': 'ầ',
  '&#7846;': 'Ầ',
  '&#7849;': 'ẩ',
  '&#7848;': 'Ẩ',
  '&#7851;': 'ẫ',
  '&#7850;': 'Ẫ',
  '&#7853;': 'ậ',
  '&#7852;': 'Ậ',
  
  '&#7871;': 'ế',
  '&#7870;': 'Ế',
  '&#7873;': 'ề',
  '&#7872;': 'Ề',
  '&#7875;': 'ể',
  '&#7874;': 'Ể',
  '&#7877;': 'ễ',
  '&#7876;': 'Ễ',
  '&#7879;': 'ệ',
  '&#7878;': 'Ệ',
  
  '&#7889;': 'ố',
  '&#7888;': 'Ố',
  '&#7891;': 'ồ',
  '&#7890;': 'Ồ',
  '&#7893;': 'ổ',
  '&#7892;': 'Ổ',
  '&#7895;': 'ỗ',
  '&#7894;': 'Ỗ',
  '&#7897;': 'ộ',
  '&#7896;': 'Ộ',
  
  // Vietnamese tone marks on horn vowels
  '&#7899;': 'ớ',
  '&#7898;': 'Ớ',
  '&#7901;': 'ờ',
  '&#7900;': 'Ờ',
  '&#7903;': 'ở',
  '&#7902;': 'Ở',
  '&#7905;': 'ỡ',
  '&#7904;': 'Ỡ',
  '&#7907;': 'ợ',
  '&#7906;': 'Ợ',
  
  '&#7913;': 'ứ',
  '&#7912;': 'Ứ',
  '&#7915;': 'ừ',
  '&#7914;': 'Ừ',
  '&#7917;': 'ử',
  '&#7916;': 'Ử',
  '&#7919;': 'ữ',
  '&#7918;': 'Ữ',
  '&#7921;': 'ự',
  '&#7920;': 'Ự',
  
  // Vietnamese tone marks on breve vowels
  '&#7855;': 'ắ',
  '&#7854;': 'Ắ',
  '&#7857;': 'ằ',
  '&#7856;': 'Ằ',
  '&#7859;': 'ẳ',
  '&#7858;': 'Ẳ',
  '&#7861;': 'ẵ',
  '&#7860;': 'Ẵ',
  '&#7863;': 'ặ',
  '&#7862;': 'Ặ',
  
  // Vietnamese tone marks on a, e, i, o, u, y
  '&#7843;': 'ả',
  '&#7842;': 'Ả',
  '&#7841;': 'ạ',
  '&#7840;': 'Ạ',
  
  '&#7867;': 'ẻ',
  '&#7866;': 'Ẻ',
  '&#7865;': 'ẹ',
  '&#7864;': 'Ẹ',
  
  '&#7881;': 'ỉ',
  '&#7880;': 'Ỉ',
  '&#7883;': 'ị',
  '&#7882;': 'Ị',
  
  '&#7887;': 'ỏ',
  '&#7886;': 'Ỏ',
  '&#7885;': 'ọ',
  '&#7884;': 'Ọ',
  
  '&#7911;': 'ủ',
  '&#7910;': 'Ủ',
  '&#7909;': 'ụ',
  '&#7908;': 'Ụ',
  
  '&#7925;': 'ỷ',
  '&#7924;': 'Ỷ',
  '&#7927;': 'ỹ',
  '&#7926;': 'Ỹ',
  '&#7923;': 'ỵ',
  '&#7922;': 'Ỵ',
  
  // Common punctuation and symbols
  '&ldquo;': '"',
  '&rdquo;': '"',
  '&lsquo;': "'",
  '&rsquo;': "'",
  '&mdash;': '—',
  '&ndash;': '–',
  '&hellip;': '…',
  '&bull;': '•',
  '&copy;': '©',
  '&reg;': '®',
  '&trade;': '™',
  '&deg;': '°',
  '&plusmn;': '±',
  '&times;': '×',
  '&divide;': '÷',
};

// Decode HTML entities in text
function decodeHtmlEntities(text) {
  let decoded = text;
  
  // Replace all known entities
  for (const [entity, char] of Object.entries(htmlEntities)) {
    decoded = decoded.split(entity).join(char);
  }
  
  // Decode numeric entities (&#XXXX;)
  decoded = decoded.replace(/&#(\d+);/g, (match, dec) => {
    return String.fromCharCode(dec);
  });
  
  // Decode hex entities (&#xXXXX;)
  decoded = decoded.replace(/&#x([0-9a-fA-F]+);/g, (match, hex) => {
    return String.fromCharCode(parseInt(hex, 16));
  });
  
  return decoded;
}

// Process all markdown files
function processDirectory(dirPath) {
  const entries = fs.readdirSync(dirPath, { withFileTypes: true });
  let processedCount = 0;
  
  for (const entry of entries) {
    const fullPath = path.join(dirPath, entry.name);
    
    if (entry.isDirectory()) {
      processedCount += processDirectory(fullPath);
    } else if (entry.isFile() && entry.name.endsWith('.md')) {
      try {
        const content = fs.readFileSync(fullPath, 'utf8');
        const decoded = decodeHtmlEntities(content);
        
        // Only write if content changed
        if (content !== decoded) {
          fs.writeFileSync(fullPath, decoded, 'utf8');
          processedCount++;
          console.log(`Fixed: ${path.relative(process.cwd(), fullPath)}`);
        }
      } catch (err) {
        console.error(`Error processing ${fullPath}: ${err.message}`);
      }
    }
  }
  
  return processedCount;
}

// Main execution
const targetDir = path.join(__dirname, 'docs', 'dcam-knowledge', 'confluence-original');

if (!fs.existsSync(targetDir)) {
  console.error(`Directory not found: ${targetDir}`);
  process.exit(1);
}

console.log('Fixing HTML entities in downloaded Confluence files...\n');
const count = processDirectory(targetDir);
console.log(`\nComplete! Fixed ${count} files.`);
