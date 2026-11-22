// K.REAPER PROJEKT — Character Select Controller
(function () {
  const panels = document.querySelectorAll('.char-panel');
  const selectBtns = document.querySelectorAll('.select-btn');
  const proceedBtn = document.getElementById('proceed-btn');
  const selectedDisplay = document.getElementById('selected-display');
  let currentHighlight = 0;
  let selectedChar = null;
  const order = ['kryss', 'nyxzara'];
  function highlight(index) {
    panels.forEach(p => p.classList.remove('highlighted'));
    const panel = document.getElementById(`panel-${order[index]}`);
    if (panel) panel.classList.add('highlighted');
    currentHighlight = index;
  }
  function selectCharacter(id) {
    selectedChar = id;
    panels.forEach(p => p.classList.remove('selected'));
    const panel = document.getElementById(`panel-${id}`);
    if (panel) panel.classList.add('selected');
    const data = window.CHARACTERS[id];
    selectedDisplay.textContent = `SELECTED: ${data.name}`;
    proceedBtn.disabled = false;
    sessionStorage.setItem('selectedCharacter', id);
    sessionStorage.setItem('selectedCharacterData', JSON.stringify(data));
  }
  selectBtns.forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.stopPropagation();
      selectCharacter(btn.dataset.char);
    });
  });
  panels.forEach(panel => {
    panel.addEventListener('click', () => {
      const id = panel.dataset.char;
      highlight(order.indexOf(id));
      selectCharacter(id);
    });
  });
  document.addEventListener('keydown', (e) => {
    if (e.key === 'ArrowLeft' || e.key === 'a') highlight((currentHighlight - 1 + order.length) % order.length);
    if (e.key === 'ArrowRight' || e.key === 'd') highlight((currentHighlight + 1) % order.length);
    if (e.key === 'Enter' || e.key === ' ') selectCharacter(order[currentHighlight]);
    if (e.key === 'Escape') {
      selectedDisplay.textContent = 'NO CHARACTER SELECTED';
      proceedBtn.disabled = true;
      panels.forEach(p => p.classList.remove('selected'));
      selectedChar = null;
    }
  });
  proceedBtn.addEventListener('click', () => {
    if (!selectedChar) return;
    alert(`ENTERING S.F.F. CLUB AS ${window.CHARACTERS[selectedChar].name}`);
  });
  highlight(0);
})();