import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { getCourse } from "../api/courseApi";
import { createProject, listProjectsByCourse } from "../api/projectApi";
import { useAuth } from "../auth/AuthContext";
import "../styles/pages.css";

export default function CoursePage() {
  const { courseId } = useParams();
  const { hasRole } = useAuth();
  const canCreate = hasRole("ROLE_TEACHER") || hasRole("ROLE_ADMIN");

  const [course, setCourse] = useState(null);
  const [projects, setProjects] = useState([]);
  const [err, setErr] = useState("");

  const [pName, setPName] = useState("");
  const [pDesc, setPDesc] = useState("");
  const [creating, setCreating] = useState(false);

  async function refresh() {
    setErr("");
    try {
      const [c, p] = await Promise.all([
        getCourse(courseId),
        listProjectsByCourse(courseId),
      ]);
      setCourse(c);
      setProjects(p || []);
    } catch (e) {
      setErr(e?.response?.data?.message || "Não foi possível carregar dados do curso.");
    }
  }

  useEffect(() => { refresh(); }, [courseId]);

  async function onCreateProject(e) {
    e.preventDefault();
    if (!canCreate) return;
    setCreating(true);
    setErr("");
    try {
      await createProject({ name: pName, description: pDesc, courseId: Number(courseId) });
      setPName(""); setPDesc("");
      await refresh();
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao criar projeto.");
    } finally {
      setCreating(false);
    }
  }

  return (
    <div className="page">
      <div className="pageHeader">
        <div>
          <h2>{course?.name || "Curso"}</h2>
          <div className="muted">{course?.description || ""}</div>
        </div>
        <div className="headerRight">
          <Link className="btn ghost" to="/dashboard">Voltar</Link>
        </div>
      </div>

      {err ? <div className="alert error">{err}</div> : null}

      {canCreate ? (
        <div className="card">
          <h3>Novo projeto</h3>
          <form className="grid2" onSubmit={onCreateProject}>
            <label>
              Nome
              <input value={pName} onChange={(e) => setPName(e.target.value)} required />
            </label>
            <label>
              Descrição
              <input value={pDesc} onChange={(e) => setPDesc(e.target.value)} />
            </label>
            <div className="actions">
              <button className="btn primary" disabled={creating}>
                {creating ? "A criar..." : "Criar projeto"}
              </button>
            </div>
          </form>
        </div>
      ) : null}

      <div className="card">
        <h3>Projetos do curso</h3>
        {projects.length === 0 ? (
          <div className="muted">Sem projetos.</div>
        ) : (
          <div className="list">
            {projects.map((p) => (
              <Link key={p.id} to={`/projects/${p.id}`} className="listRow">
                <div className="rowMain">
                  <div className="rowTitle">{p.name}</div>
                  <div className="rowSub muted">{p.description || "Sem descrição"}</div>
                </div>
                <div className="rowMeta muted">#{p.id}</div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
