/* evaluation.js
   Coleta respostas de usabilidade em escala Likert (1..5)
   Salva em LocalStorage (key: 'aed3_evaluations')
   Permite exportar JSON/CSV, mostrar resumo e apagar respostas.
*/

(function () {
  'use strict';

  const STORAGE_KEY = 'aed3_evaluations';

  // DOM
  const form = document.getElementById('surveyForm');
  const btnClearForm = document.getElementById('btnClearForm');
  const btnShowAll = document.getElementById('btnShowAll');
  const btnSummary = document.getElementById('btnSummary');
  const btnExportJSON = document.getElementById('btnExportJSON');
  const btnExportCSV = document.getElementById('btnExportCSV');
  const btnClearAll = document.getElementById('btnClearAll');
  const responsesTableBody = document.querySelector('#responsesTable tbody');
  const summaryDiv = document.getElementById('summary');

  // fields
  const participantName = document.getElementById('participantName');
  const participantCourse = document.getElementById('participantCourse');
  const qIds = ['q1','q2','q3','q4','q5','q6','q7'];
  const comments = document.getElementById('comments');

  function loadResponses() {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    try {
      const arr = JSON.parse(raw);
      if (!Array.isArray(arr)) return [];
      return arr;
    } catch (e) {
      console.error('Erro ao parsear avaliações', e);
      return [];
    }
  }

  function saveResponses(list) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
  }

  function addResponse(resp) {
    const list = loadResponses();
    list.push(resp);
    saveResponses(list);
  }

  function clearAllResponses() {
    localStorage.removeItem(STORAGE_KEY);
    renderTable([]);
    summaryDiv.style.display = 'none';
    alert('Todas as respostas foram apagadas.');
  }

  function renderTable(list) {
    responsesTableBody.innerHTML = '';
    if (!list.length) {
      const tr = document.createElement('tr');
      tr.innerHTML = '<td colspan="12" style="color:#666">Nenhuma resposta coletada.</td>';
      responsesTableBody.appendChild(tr);
      return;
    }
    list.forEach((r, idx) => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${idx+1}</td>
        <td>${escapeHtml(r.name || '')}</td>
        <td>${escapeHtml(r.course || '')}</td>
        <td>${escapeHtml(new Date(r.date).toLocaleString())}</td>
        <td>${r.q1}</td><td>${r.q2}</td><td>${r.q3}</td><td>${r.q4}</td><td>${r.q5}</td><td>${r.q6}</td><td>${r.q7}</td>
        <td>${escapeHtml(r.comments || '')}</td>
      `;
      responsesTableBody.appendChild(tr);
    });
  }

  function computeSummary(list) {
    if (!list.length) return null;
    const sums = { q1:0,q2:0,q3:0,q4:0,q5:0,q6:0,q7:0 };
    list.forEach(r => {
      sums.q1 += Number(r.q1)||0;
      sums.q2 += Number(r.q2)||0;
      sums.q3 += Number(r.q3)||0;
      sums.q4 += Number(r.q4)||0;
      sums.q5 += Number(r.q5)||0;
      sums.q6 += Number(r.q6)||0;
      sums.q7 += Number(r.q7)||0;
    });
    const n = list.length;
    return {
      n,
      avg: {
        q1: (sums.q1/n).toFixed(2),
        q2: (sums.q2/n).toFixed(2),
        q3: (sums.q3/n).toFixed(2),
        q4: (sums.q4/n).toFixed(2),
        q5: (sums.q5/n).toFixed(2),
        q6: (sums.q6/n).toFixed(2),
        q7: (sums.q7/n).toFixed(2)
      }
    };
  }

  function showSummary() {
    const list = loadResponses();
    const s = computeSummary(list);
    if (!s) {
      summaryDiv.style.display = 'block';
      summaryDiv.innerHTML = '<strong>Nenhuma resposta para resumir.</strong>';
      return;
    }
    summaryDiv.style.display = 'block';
    summaryDiv.innerHTML = `
      <strong>Respostas coletadas:</strong> ${s.n} <br/>
      <table style="margin-top:8px; border-collapse:collapse;">
        <tr><td style="padding:6px 10px">1. Utilidade (armazenamento)</td><td style="padding:6px 10px">${s.avg.q1}</td></tr>
        <tr><td style="padding:6px 10px">2. Facilidade das funções</td><td style="padding:6px 10px">${s.avg.q2}</td></tr>
        <tr><td style="padding:6px 10px">3. Clareza das mensagens</td><td style="padding:6px 10px">${s.avg.q3}</td></tr>
        <tr><td style="padding:6px 10px">4. Intuitividade</td><td style="padding:6px 10px">${s.avg.q4}</td></tr>
        <tr><td style="padding:6px 10px">5. Fluxo de edição</td><td style="padding:6px 10px">${s.avg.q5}</td></tr>
        <tr><td style="padding:6px 10px">6. Export/Import</td><td style="padding:6px 10px">${s.avg.q6}</td></tr>
        <tr><td style="padding:6px 10px">7. Satisfação geral</td><td style="padding:6px 10px">${s.avg.q7}</td></tr>
      </table>
      <p style="margin-top:8px; font-size:13px; color:#333;">Use <em>Exportar CSV</em> para anexar em planilha com as respostas individuais.</p>
    `;
  }

  function exportJSON() {
    const list = loadResponses();
    const blob = new Blob([JSON.stringify(list, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `aed3_evaluations_${new Date().toISOString().slice(0,10)}.json`;
    a.click();
    URL.revokeObjectURL(url);
  }

  function exportCSV() {
    const list = loadResponses();
    if (!list.length) {
      alert('Nenhuma resposta para exportar.');
      return;
    }
    const header = ['idx','name','course','date','q1','q2','q3','q4','q5','q6','q7','comments'];
    const rows = list.map((r, i) => [
      i+1,
      csvEscape(r.name || ''),
      csvEscape(r.course || ''),
      csvEscape(r.date || ''),
      r.q1, r.q2, r.q3, r.q4, r.q5, r.q6, r.q7,
      csvEscape(r.comments || '')
    ]);
    const csv = [header.join(','), ...rows.map(r => r.join(','))].join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `aed3_evaluations_${new Date().toISOString().slice(0,10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  }

  function csvEscape(s) {
    if (s == null) return '';
    const v = String(s).replace(/"/g, '""');
    if (v.includes(',') || v.includes('\n') || v.includes('"')) return `"${v}"`;
    return v;
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

  // submit
  form.addEventListener('submit', function (ev) {
    ev.preventDefault();
    // validações: todos os q's são required (selects)
    const values = {};
    for (const q of qIds) {
      const el = document.getElementById(q);
      if (!el || !el.value) {
        alert('Por favor responda todas as afirmações (selecionar 1..5).');
        el && el.focus();
        return;
      }
      values[q] = Number(el.value);
    }

    const resp = {
      id: Date.now(),
      name: participantName.value.trim(),
      course: participantCourse.value.trim(),
      date: new Date().toISOString(),
      q1: values.q1,
      q2: values.q2,
      q3: values.q3,
      q4: values.q4,
      q5: values.q5,
      q6: values.q6,
      q7: values.q7,
      comments: comments.value.trim()
    };

    addResponse(resp);
    // Atualizar UI
    renderTable(loadResponses());
    // mostrar resumo parcialmente
    showSummary();
    // feedback
    alert('Resposta registrada — obrigado(a)!');
    form.reset();
  });

  btnClearForm.addEventListener('click', function () {
    if (confirm('Limpar o formulário?')) form.reset();
  });

  btnShowAll.addEventListener('click', function () {
    const list = loadResponses();
    renderTable(list);
    summaryDiv.style.display = 'none';
  });

  btnSummary.addEventListener('click', function () {
    showSummary();
  });

  btnExportJSON.addEventListener('click', exportJSON);
  btnExportCSV.addEventListener('click', exportCSV);

  btnClearAll.addEventListener('click', function () {
    if (!confirm('Apagar todas as respostas?')) return;
    clearAllResponses();
  });

  // init
  renderTable(loadResponses());

})();
