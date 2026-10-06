const $ = id => document.getElementById(id);
async function loadSummary(){
  try{
    const d=await (await fetch('/api/summary')).json();
    $('conversations').textContent=d.conversations.toLocaleString();
    $('messages').textContent=d.messages.toLocaleString();
    $('patterns').textContent=d.patterns.toLocaleString();
    $('flagged').textContent=d.flagged.toLocaleString();
  }catch(e){$('conversations').textContent='Server off';}
}
$('sampleBtn').onclick=()=>{$('conversation').value=
`Alice: I received a suspicious link asking me to verify my company account.
Bob: Do not enter your credentials. It may be a phishing attempt.
Alice: I already entered my password and noticed an unauthorized login.
Bob: Change your password, enable MFA, and report the incident.`;};
$('analyzeBtn').onclick=async()=>{
  const text=$('conversation').value.trim(); if(!text){alert('Paste a conversation first.');return;}
  const b=$('analyzeBtn');b.disabled=true;b.textContent='Analyzing…';
  try{
    const r=await (await fetch('/api/analyze',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({text})})).json();
    const el=$('result');el.classList.remove('hidden');
    const flagged=r.score>=10;
    const cats=Object.entries(r.categories);
    el.innerHTML=`<h3>${flagged?'⚠️':'✓'} ${r.status} <span class="pill ${flagged?'alert':''}">Review score: ${r.score}</span></h3>
      <div>${cats.length?cats.map(([c,n])=>`<span class="pill">${escapeHtml(c)} · ${n}</span>`).join(''):'<span class="muted">No catalogued patterns matched.</span>'}</div>
      <div class="matches">${r.matches.map(m=>`<div class="match"><b>${escapeHtml(m.pattern)}</b> — ${escapeHtml(m.category)} · chars ${m.start}–${m.end}</div>`).join('')}</div>
      <p class="muted">This is a keyword-based indicator score, not a determination that a person acted maliciously.</p>`;
    if(flagged && 'Notification' in window && Notification.permission==='granted') new Notification('CyberWatch: review recommended',{body:'Potential indicators found in the analyzed conversation.'});
    if(flagged && 'Notification' in window && Notification.permission==='default') Notification.requestPermission();
  }catch(e){alert('Could not reach the local server. Start WebServer.java first.');}
  finally{b.disabled=false;b.textContent='Analyze conversation ↗';}
};
$('rescanBtn').onclick=async()=>{
  $('datasetResult').textContent='Scanning bundled dataset…';
  try{const d=await(await fetch('/api/rescan',{method:'POST'})).json();
    $('datasetResult').textContent=`Scanned ${d.conversations.toLocaleString()} conversations and ${d.messages.toLocaleString()} messages. ${d.flagged.toLocaleString()} conversations met the review threshold.`;
  }catch(e){$('datasetResult').textContent='Could not reach server.';}
};
function escapeHtml(s){return String(s).replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));}
loadSummary();
