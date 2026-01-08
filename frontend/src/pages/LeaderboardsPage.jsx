import React, { useEffect, useState } from "react";
import { getLeaderboard, getAverage } from "../api/achievementApi";
import { getTeamLeaderboard } from "../api/teamApi";
import "../styles/pages.css";

export default function LeaderboardsPage() {
  const [lb, setLb] = useState([]);
  const [teamLb, setTeamLb] = useState([]);
  const [avg, setAvg] = useState(null);
  const [err, setErr] = useState("");

  async function refresh() {
    setErr("");
    try {
      const [l, t, a] = await Promise.all([getLeaderboard(), getTeamLeaderboard(), getAverage()]);
      setLb(l || []);
      setTeamLb(t || []);
      setAvg(a ?? null);
    } catch (e) {
      setErr(e?.response?.data?.message || "Não foi possível carregar rankings.");
    }
  }

  useEffect(() => { refresh(); }, []);

  return (
    <div className="page">
      <div className="pageHeader">
        <div>
          <h2>Rankings</h2>
          <div className="muted">Pontuação agregada por aluno e por equipa.</div>
        </div>
      </div>

      {err ? <div className="alert error">{err}</div> : null}

      <div className="grid">
        <div className="card">
          <h3>Leaderboard (Alunos)</h3>
          {avg !== null ? <div className="muted">Média: {avg}</div> : null}
          <div className="tableWrap">
            <table className="table">
              <thead>
                <tr><th>Aluno</th><th>Total</th></tr>
              </thead>
              <tbody>
                {lb.map((x, i) => (
                  <tr key={i}>
                    <td>{x.studentName}</td>
                    <td>{x.totalPoints}</td>
                  </tr>
                ))}
                {lb.length === 0 ? <tr><td colSpan="2" className="muted">Sem dados.</td></tr> : null}
              </tbody>
            </table>
          </div>
        </div>

        <div className="card">
          <h3>Leaderboard (Equipas)</h3>
          <div className="tableWrap">
            <table className="table">
              <thead>
                <tr><th>Equipa</th><th>Projeto</th><th>Total</th></tr>
              </thead>
              <tbody>
                {teamLb.map((x, i) => (
                  <tr key={i}>
                    <td>{x.teamName}</td>
                    <td>{x.projectName}</td>
                    <td>{x.totalPoints}</td>
                  </tr>
                ))}
                {teamLb.length === 0 ? <tr><td colSpan="3" className="muted">Sem dados.</td></tr> : null}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <button className="btn" onClick={refresh}>Recarregar</button>
    </div>
  );
}
