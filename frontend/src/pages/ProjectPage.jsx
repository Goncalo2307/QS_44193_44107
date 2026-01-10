import React, { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getProject } from "../api/projectApi";
import { createSprint, deleteSprint, listSprints } from "../api/sprintApi";
import { createTask, listTasksBySprint, updateTaskStatus, assignTask } from "../api/taskApi";
import { createTeam, getTeamByProject, listMembers, addMember, removeMember } from "../api/teamApi";
import { useAuth } from "../auth/AuthContext";
import "../styles/pages.css";

const STATUS_OPTIONS = ["TO_DO", "IN_PROGRESS", "DONE"];
const SCRUM_ROLE_OPTIONS = ["SCRUM_MASTER", "PRODUCT_OWNER", "DEVELOPER"];

export default function ProjectPage() {
  const { projectId } = useParams();
  const { hasRole } = useAuth();
  const canManage = hasRole("ROLE_TEACHER") || hasRole("ROLE_ADMIN");

  const [project, setProject] = useState(null);
  const [sprints, setSprints] = useState([]);
  const [selectedSprintId, setSelectedSprintId] = useState(null);
  const [tasks, setTasks] = useState([]);

  const [team, setTeam] = useState(null);
  const [members, setMembers] = useState([]);

  const [err, setErr] = useState("");

  // Create sprint form
  const [sName, setSName] = useState("");
  const [sGoal, setSGoal] = useState("");
  const [sStart, setSStart] = useState("");
  const [sEnd, setSEnd] = useState("");

  // Create task form
  const [tTitle, setTTitle] = useState("");
  const [tDesc, setTDesc] = useState("");
  const [tPoints, setTPoints] = useState(1);

  // Team form
  const [teamName, setTeamName] = useState("");
  const [memberUserId, setMemberUserId] = useState("");
  const [memberRole, setMemberRole] = useState("DEVELOPER");

  async function refreshProject() {
    setErr("");
    try {
      const p = await getProject(projectId);
      setProject(p);
    } catch (e) {
      setErr(e?.response?.data?.message || "Não foi possível carregar o projeto.");
    }
  }

  async function refreshSprints() {
    try {
      const sp = await listSprints(projectId);
      setSprints(sp || []);
      const first = sp?.[0]?.id || null;
      setSelectedSprintId((prev) => prev || first);
    } catch (e) {
      setErr(e?.response?.data?.message || "Não foi possível carregar sprints.");
    }
  }

  async function refreshTasks(sprintId) {
    if (!sprintId) { setTasks([]); return; }
    try {
      const t = await listTasksBySprint(sprintId);
      setTasks(t || []);
    } catch (e) {
      setErr(e?.response?.data?.message || "Não foi possível carregar tarefas.");
    }
  }

  async function refreshTeam() {
    try {
      const data = await getTeamByProject(projectId);
      // backend pode devolver lista -> escolhemos 1º
      const normalized = Array.isArray(data) ? data[0] : data;
      setTeam(normalized || null);
      if (normalized?.id) {
        const m = await listMembers(normalized.id);
        setMembers(m || []);
      } else {
        setMembers([]);
      }
    } catch (e) {
      // se não existir equipa ainda, não é erro fatal
      setTeam(null);
      setMembers([]);
    }
  }

  useEffect(() => {
    refreshProject();
    refreshSprints();
    refreshTeam();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [projectId]);

  useEffect(() => {
    refreshTasks(selectedSprintId);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedSprintId]);

  const selectedSprint = useMemo(
    () => sprints.find((s) => String(s.id) === String(selectedSprintId)) || null,
    [sprints, selectedSprintId]
  );

  async function onCreateSprint(e) {
    e.preventDefault();
    if (!canManage) return;
    setErr("");
    try {
      await createSprint(projectId, { name: sName, goal: sGoal, startDate: sStart, endDate: sEnd });
      setSName(""); setSGoal(""); setSStart(""); setSEnd("");
      await refreshSprints();
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao criar sprint.");
    }
  }

  async function onDeleteSprint(id) {
    if (!canManage) return;
    setErr("");
    try {
      await deleteSprint(id);
      setSelectedSprintId(null);
      await refreshSprints();
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao remover sprint.");
    }
  }

  async function onCreateTask(e) {
    e.preventDefault();
    if (!selectedSprintId) return;
    setErr("");
    try {
      await createTask(selectedSprintId, {
        title: tTitle,
        description: tDesc,
        status: "TO_DO",
        estimatedPoints: Number(tPoints),
      });
      setTTitle(""); setTDesc(""); setTPoints(1);
      await refreshTasks(selectedSprintId);
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao criar tarefa.");
    }
  }

  async function onStatusChange(taskId, newStatus) {
    setErr("");
    try {
      await updateTaskStatus(taskId, newStatus);
      await refreshTasks(selectedSprintId);
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao atualizar estado.");
    }
  }

  async function onAssign(taskId) {
    const userId = prompt("ID do utilizador para atribuir esta tarefa:");
    if (!userId) return;
    setErr("");
    try {
      await assignTask(taskId, userId);
      await refreshTasks(selectedSprintId);
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao atribuir tarefa.");
    }
  }

  async function onCreateTeam(e) {
    e.preventDefault();
    if (!canManage) return;
    setErr("");
    try {
      await createTeam(projectId, teamName);
      setTeamName("");
      await refreshTeam();
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao criar equipa.");
    }
  }

  async function onAddMember(e) {
    e.preventDefault();
    if (!team?.id) return;
    setErr("");
    try {
      await addMember(team.id, memberUserId, memberRole);
      setMemberUserId("");
      setMemberRole("DEVELOPER");
      await refreshTeam();
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao adicionar membro.");
    }
  }

  async function onRemoveMember(id) {
    if (!canManage) return;
    setErr("");
    try {
      await removeMember(id);
      await refreshTeam();
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao remover membro.");
    }
  }

  return (
    <div className="page">
      <div className="pageHeader">
        <div>
          <h2>{project?.name || "Projeto"}</h2>
          <div className="muted">{project?.description || ""}</div>
        </div>
        <div className="headerRight">
          <Link className="btn ghost" to="/dashboard">Voltar</Link>
        </div>
      </div>

      {err ? <div className="alert error">{err}</div> : null}

      <div className="grid">
        <div className="card">
          <h3>Sprints</h3>

          {canManage ? (
            <form className="stack" onSubmit={onCreateSprint}>
              <div className="grid2">
                <label>
                  Nome
                  <input value={sName} onChange={(e) => setSName(e.target.value)} required />
                </label>
                <label>
                  Objetivo
                  <input value={sGoal} onChange={(e) => setSGoal(e.target.value)} />
                </label>
                <label>
                  Início
                  <input value={sStart} onChange={(e) => setSStart(e.target.value)} type="date" />
                </label>
                <label>
                  Fim
                  <input value={sEnd} onChange={(e) => setSEnd(e.target.value)} type="date" />
                </label>
              </div>
              <button className="btn primary">Criar sprint</button>
            </form>
          ) : null}

          <div className="list">
            {sprints.map((s) => (
              <div key={s.id} className={"listRow compact " + (String(selectedSprintId) === String(s.id) ? "selected" : "")}>
                <button className="linkButton" onClick={() => setSelectedSprintId(s.id)}>
                  <div className="rowMain">
                    <div className="rowTitle">{s.name}</div>
                    <div className="rowSub muted">{s.goal || "Sem objetivo"}</div>
                  </div>
                </button>
                {canManage ? (
                  <button className="btn danger small" onClick={() => onDeleteSprint(s.id)}>Remover</button>
                ) : null}
              </div>
            ))}
          </div>
        </div>

        <div className="card">
          <h3>Backlog {selectedSprint ? `— ${selectedSprint.name}` : ""}</h3>

          {selectedSprintId ? (
            <>
              <form className="stack" onSubmit={onCreateTask}>
                <div className="grid2">
                  <label>
                    Título
                    <input value={tTitle} onChange={(e) => setTTitle(e.target.value)} required />
                  </label>
                  <label>
                    Pontos
                    <input value={tPoints} onChange={(e) => setTPoints(e.target.value)} type="number" min="1" />
                  </label>
                </div>
                <label>
                  Descrição
                  <input value={tDesc} onChange={(e) => setTDesc(e.target.value)} />
                </label>
                <button className="btn primary">Adicionar tarefa</button>
              </form>

              <div className="tableWrap">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Tarefa</th>
                      <th>Estado</th>
                      <th>Pontos</th>
                      <th>Atribuição</th>
                    </tr>
                  </thead>
                  <tbody>
                    {tasks.map((t) => (
                      <tr key={t.id}>
                        <td>
                          <div className="rowTitle">{t.title}</div>
                          <div className="muted">{t.description || ""}</div>
                        </td>
                        <td>
                          <select value={t.status} onChange={(e) => onStatusChange(t.id, e.target.value)}>
                            {STATUS_OPTIONS.map((s) => <option key={s} value={s}>{s}</option>)}
                          </select>
                        </td>
                        <td>{t.estimatedPoints ?? "-"}</td>
                        <td>
                          <button className="btn small" onClick={() => onAssign(t.id)}>
                            Atribuir
                          </button>
                        </td>
                      </tr>
                    ))}
                    {tasks.length === 0 ? (
                      <tr><td colSpan="4" className="muted">Sem tarefas.</td></tr>
                    ) : null}
                  </tbody>
                </table>
              </div>
            </>
          ) : (
            <div className="muted">Seleciona uma sprint para ver o backlog.</div>
          )}
        </div>
      </div>

      <div className="card">
        <h3>Equipa Scrum</h3>

        {!team ? (
          canManage ? (
            <form className="grid2" onSubmit={onCreateTeam}>
              <label>
                Nome da equipa
                <input value={teamName} onChange={(e) => setTeamName(e.target.value)} required />
              </label>
              <div className="actions">
                <button className="btn primary">Criar equipa</button>
              </div>
            </form>
          ) : (
            <div className="muted">Ainda não existe equipa para este projeto.</div>
          )
        ) : (
          <>
            <div className="muted">Equipa: <strong>{team.name}</strong> (#{team.id})</div>

            <div className="grid">
              <div className="card subtle">
                <h4>Adicionar membro</h4>
                <form className="grid2" onSubmit={onAddMember}>
                  <label>
                    ID Utilizador
                    <input value={memberUserId} onChange={(e) => setMemberUserId(e.target.value)} required />
                  </label>
                  <label>
                    Papel
                    <select value={memberRole} onChange={(e) => setMemberRole(e.target.value)}>
                      {SCRUM_ROLE_OPTIONS.map((r) => <option key={r} value={r}>{r}</option>)}
                    </select>
                  </label>
                  <div className="actions">
                    <button className="btn primary">Adicionar</button>
                  </div>
                </form>
              </div>

              <div className="card subtle">
                <h4>Membros</h4>
                <div className="list">
                  {members.map((m) => (
                    <div key={m.id} className="listRow compact">
                      <div className="rowMain">
                        <div className="rowTitle">{m.account?.name || "Utilizador"}</div>
                        <div className="rowSub muted">{m.scrumRole}</div>
                      </div>
                      {canManage ? (
                        <button className="btn danger small" onClick={() => onRemoveMember(m.id)}>Remover</button>
                      ) : null}
                    </div>
                  ))}
                  {members.length === 0 ? <div className="muted">Sem membros.</div> : null}
                </div>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
