/* script.js
   CRUD para Produto (id, gtin13, nome, descricao, ativo)
   Armazenamento: LocalStorage (key: 'aed3_produtos')
*/

(function () {
  'use strict';

  const STORAGE_KEY = 'aed3_produtos';

  // Elementos DOM
  const form = document.getElementById('produtoForm');
  const inpId = document.getElementById('prodId');
  const inpGtin = document.getElementById('gtin13');
  const inpNome = document.getElementById('nome');
  const inpDesc = document.getElementById('descricao');
  const inpAtivo = document.getElementById('ativo');

  const tableBody = document.querySelector('#produtosTable tbody');
  const searchInput = document.getElementById('search');
  const btnSearch = document.getElementById('btnSearch');
  const btnReset = document.getElementById('btnReset');
  const exportBtn = document.getElementById('exportBtn');
  const importBtn = document.getElementById('importBtn');
  const importInput = document.getElementById('importInput');
  const clearAllBtn = document.getElementById('clearAllBtn');
  const limparFormBtn = document.getElementById('limparForm');
  const statsDiv = document.getElementById('stats');
  const showRawBtn = document.getElementById('showRawBtn');
  const rawArea = document.getElementById('rawArea');
  const rawJson = document.getElementById('rawJson');

  // ---------- Storage helpers ----------
  function loadProducts() {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    try {
      const arr = JSON.parse(raw);
      if (!Array.isArray(arr)) return [];
      return arr;
    } catch (e) {
      console.error('Erro ao parsear LocalStorage', e);
      return [];
    }
  }

  function saveProducts(list) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
  }

  function getNextId() {
    // tenta usar o maior id atual + 1
    const list = loadProducts();
    const max = list.reduce((m, p) => Math.max(m, Number(p.id) || 0), 0);
    return max + 1;
  }

  // ---------- Render ----------
  function renderTable(list) {
    tableBody.innerHTML = '';
    if (!list.length) {
      const tr = document.createElement('tr');
      tr.innerHTML = '<td colspan="6" style="color:#666">Nenhum produto cadastrado.</td>';
      tableBody.appendChild(tr);
      updateStats(list);
      return;
    }

    for (const p of list) {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${escapeHtml(String(p.id))}</td>
        <td>${escapeHtml(p.gtin13 || '')}</td>
        <td>${escapeHtml(p.nome || '')}</td>
        <td>${escapeHtml(p.descricao || '')}</td>
        <td>${p.ativo ? 'Sim' : 'Não'}</td>
        <td class="actions">
          <button class="small" data-action="edit" data-id="${p.id}">Editar</button>
          <button class="small danger" data-action="delete" data-id="${p.id}">Excluir</button>
        </td>
      `;
      tableBody.appendChild(tr);
    }
    updateStats(list);
  }

  function updateStats(list) {
    const total = list.length;
    const ativos = list.filter(p => p.ativo).length;
    statsDiv.textContent = `Registros: ${total} • Ativos: ${ativos}`;
  }

  function escapeHtml(text) {
    if (text == null) return '';
    return String(text)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  // ---------- CRUD ----------
  function upsertProduct(prod) {
    const list = loadProducts();
    const idx = list.findIndex(x => String(x.id) === String(prod.id));
    if (idx >= 0) list[idx] = prod;
    else list.push(prod);
    saveProducts(list);
    renderTable(list);
  }

  function deleteProductById(id) {
    let list = loadProducts();
    list = list.filter(p => String(p.id) !== String(id));
    saveProducts(list);
    renderTable(list);
  }

  function fillForm(prod) {
    inpId.value = prod.id;
    inpGtin.value = prod.gtin13 || '';
    inpNome.value = prod.nome || '';
    inpDesc.value = prod.descricao || '';
    inpAtivo.checked = !!prod.ativo;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  function clearForm() {
    inpId.value = '';
    form.reset();
  }

  // ---------- Busca ----------
  function searchProducts(term) {
    term = (term || '').trim().toLowerCase();
    const list = loadProducts();
    if (!term) return list;
    return list.filter(p =>
      (p.gtin13 || '').toLowerCase().includes(term) ||
      (p.nome || '').toLowerCase().includes(term) ||
      (p.descricao || '').toLowerCase().includes(term)
    );
  }

  // ---------- Export / Import ----------
  function exportJSON() {
    const list = loadProducts();
    const blob = new Blob([JSON.stringify(list, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'aed3_produtos_backup.json';
    a.click();
    URL.revokeObjectURL(url);
  }

  function importJSONFile(file) {
    if (!file) return;
    const reader = new FileReader();
    reader.onload = function (e) {
      try {
        const imported = JSON.parse(e.target.result);
        if (!Array.isArray(imported)) throw new Error('Arquivo inválido (esperado JSON array)');
        // Mesclar: manter ids quando presentes, caso contrário gerar novo id
        const current = loadProducts();
        const merged = [...current];
        for (const it of imported) {
          if (!it.id) it.id = getNextId();
          const idx = merged.findIndex(x => String(x.id) === String(it.id));
          if (idx >= 0) merged[idx] = it;
          else merged.push(it);
        }
        saveProducts(merged);
        renderTable(merged);
        alert('Importação concluída.');
      } catch (err) {
        alert('Erro ao importar: ' + err.message);
      }
    };
    reader.readAsText(file);
  }

  // ---------- Eventos ----------
  form.addEventListener('submit', function (ev) {
    ev.preventDefault();
    // Validações básicas
    const nome = inpNome.value.trim();
    if (!nome) {
      alert('Nome é obrigatório');
      inpNome.focus();
      return;
    }

    let id = inpId.value ? Number(inpId.value) : null;
    if (!id) id = getNextId();

    const produto = {
      id: id,
      gtin13: inpGtin.value.trim(),
      nome: nome,
      descricao: inpDesc.value.trim(),
      ativo: !!inpAtivo.checked
    };

    upsertProduct(produto);
    clearForm();
  });

  tableBody.addEventListener('click', function (ev) {
    const btn = ev.target.closest('button');
    if (!btn) return;
    const action = btn.dataset.action;
    const id = btn.dataset.id;
    if (action === 'edit') {
      const list = loadProducts();
      const prod = list.find(p => String(p.id) === String(id));
      if (prod) fillForm(prod);
    } else if (action === 'delete') {
      if (confirm('Confirma exclusão deste produto?')) {
        deleteProductById(id);
      }
    }
  });

  btnSearch.addEventListener('click', function () {
    const term = searchInput.value;
    renderTable(searchProducts(term));
  });

  btnReset.addEventListener('click', function () {
    searchInput.value = '';
    renderTable(loadProducts());
  });

  exportBtn.addEventListener('click', exportJSON);

  importBtn.addEventListener('click', function () {
    importInput.click();
  });

  importInput.addEventListener('change', function (ev) {
    const f = ev.target.files[0];
    importJSONFile(f);
    importInput.value = '';
  });

  clearAllBtn.addEventListener('click', function () {
    if (!confirm('Apagar todos os produtos? Esta ação não pode ser desfeita.')) return;
    localStorage.removeItem(STORAGE_KEY);
    renderTable([]);
  });

  limparFormBtn.addEventListener('click', clearForm);

  showRawBtn.addEventListener('click', function () {
    const list = loadProducts();
    if (rawArea.style.display === 'none' || rawArea.style.display === '') {
      rawJson.value = JSON.stringify(list, null, 2);
      rawArea.style.display = 'block';
      showRawBtn.textContent = 'Ocultar JSON (raw)';
    } else {
      rawArea.style.display = 'none';
      showRawBtn.textContent = 'Mostrar JSON (raw)';
    }
  });

  // Inicialização
  function init() {
    renderTable(loadProducts());
  }

  init();

})();
