import React, { useEffect, useState } from "react";
import { assignBadge, createBadge, listBadges } from "../api/achievementApi";
import { useAuth } from "../auth/AuthContext";
import "../styles/pages.css";

export default function BadgesPage() {
  const { hasRole } = useAuth();
  const canManage = hasRole("ROLE_TEACHER") || hasRole("ROLE_ADMIN");

  const [badges, setBadges] = useState([]);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");

  const [bName, setBName] = useState("");
  const [bDesc, setBDesc] = useState("");
  const [bPoints, setBPoints] = useState(1);

  const [studentEmail, setStudentEmail] = useState("");
  const [selectedId, setSelectedId] = useState("");

  async function refresh() {
    setErr(""); setOk("");
    try {
      const data = await listBadges();
      setBadges(data || []);
      if (!selectedId && data?.[0]?.id) setSelectedId(String(data[0].id));
    } catch (e) {
      setErr(e?.response?.data?.message || "Não foi possível carregar distinções.");
    }
  }

  useEffect(() => { refresh(); }, []);

  async function onCreate(e) {
    e.preventDefault();
    if (!canManage) return;
    setErr(""); setOk("");
    try {
      await createBadge({ name: bName, description: bDesc, points: Number(bPoints) });
      setBName(""); setBDesc(""); setBPoints(1);
      setOk("Distinção criada.");
      await refresh();
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao criar distinção.");
    }
  }

  async function onAssign(e) {
    e.preventDefault();
    if (!canManage) return;
    setErr(""); setOk("");
    try {
      await assignBadge(studentEmail, Number(selectedId));
      setStudentEmail("");
      setOk("Distinção atribuída.");
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao atribuir distinção.");
    }
  }

  return (
    <div className="page">
      <div className="pageHeader">
        <div>
          <h2>Distinções</h2>
          <div className="muted">Criação e atribuição de badges/achievements.</div>
        </div>
      </div>

      {err ? <div className="alert error">{err}</div> : null}
      {ok ? <div className="alert ok">{ok}</div> : null}

      <div className="grid">
        <div className="card">
          <h3>Catálogo</h3>
          <div className="list">
            {badges.map((b) => (
              <div key={b.id} className="listRow compact">
                <div className="rowMain">
                  <div className="rowTitle">{b.name} <span className="chip">{b.points} pts</span></div>
                  <div className="rowSub muted">{b.description || "Sem descrição"}</div>
                </div>
                <div className="rowMeta muted">#{b.id}</div>
              </div>
            ))}
            {badges.length === 0 ? <div className="muted">Sem distinções.</div> : null}
          </div>
        </div>

        <div className="card">
          <h3>Criar distinção</h3>
          {!canManage ? (
            <div className="muted">Apenas Professor/Admin pode criar.</div>
          ) : (
            <form className="stack" onSubmit={onCreate}>
              <label>Nome<input value={bName} onChange={(e) => setBName(e.target.value)} required /></label>
              <label>Descrição<input value={bDesc} onChange={(e) => setBDesc(e.target.value)} /></label>
              <label>Pontos<input type="number" min="1" value={bPoints} onChange={(e) => setBPoints(e.target.value)} /></label>
              <button className="btn primary">Criar</button>
            </form>
          )}
        </div>

        <div className="card">
          <h3>Atribuir distinção</h3>
          {!canManage ? (
            <div className="muted">Apenas Professor/Admin pode atribuir.</div>
          ) : (
            <form className="stack" onSubmit={onAssign}>
              <label>Email do aluno<input value={studentEmail} onChange={(e) => setStudentEmail(e.target.value)} type="email" required /></label>
              <label>Distinção
                <select value={selectedId} onChange={(e) => setSelectedId(e.target.value)}>
                  {badges.map((b) => <option key={b.id} value={b.id}>{b.name} ({b.points} pts)</option>)}
                </select>
              </label>
              <button className="btn primary">Atribuir</button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
