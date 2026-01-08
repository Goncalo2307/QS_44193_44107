import React, { useEffect, useMemo, useState } from "react";
import { createCourse, listCourses } from "../api/courseApi";
import { useAuth } from "../auth/AuthContext";
import "../styles/pages.css";
import { Link } from "react-router-dom";

export default function Dashboard() {
  const { hasRole } = useAuth();
  const canCreate = hasRole("ROLE_TEACHER") || hasRole("ROLE_ADMIN");

  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [creating, setCreating] = useState(false);

  async function refresh() {
    setErr("");
    setLoading(true);
    try {
      const data = await listCourses();
      setCourses(data || []);
    } catch (e) {
      setErr(e?.response?.data?.message || "Não foi possível carregar cursos.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { refresh(); }, []);

  const stats = useMemo(() => ({
    total: courses.length,
  }), [courses]);

  async function onCreate(e) {
    e.preventDefault();
    if (!canCreate) return;
    setCreating(true);
    try {
      await createCourse({ name, description });
      setName("");
      setDescription("");
      await refresh();
    } catch (e) {
      setErr(e?.response?.data?.message || "Falha ao criar curso.");
    } finally {
      setCreating(false);
    }
  }

  return (
    <div className="page">
      <div className="pageHeader">
        <div>
          <h2>Cursos</h2>
          <div className="muted">Total: {stats.total}</div>
        </div>
      </div>

      {err ? <div className="alert error">{err}</div> : null}

      {canCreate ? (
        <div className="card">
          <h3>Novo curso</h3>
          <form className="grid2" onSubmit={onCreate}>
            <label>
              Nome
              <input value={name} onChange={(e) => setName(e.target.value)} required />
            </label>
            <label>
              Descrição
              <input value={description} onChange={(e) => setDescription(e.target.value)} />
            </label>
            <div className="actions">
              <button className="btn primary" disabled={creating}>
                {creating ? "A criar..." : "Criar"}
              </button>
            </div>
          </form>
        </div>
      ) : (
        <div className="card subtle">
          <div className="muted">Sem permissões para criar cursos (apenas Professor/Admin).</div>
        </div>
      )}

      <div className="card">
        <h3>Lista de cursos</h3>
        {loading ? (
          <div className="muted">A carregar...</div>
        ) : courses.length === 0 ? (
          <div className="muted">Ainda não existem cursos.</div>
        ) : (
          <div className="list">
            {courses.map((c) => (
              <Link key={c.id} to={`/courses/${c.id}`} className="listRow">
                <div className="rowMain">
                  <div className="rowTitle">{c.name}</div>
                  <div className="rowSub muted">{c.description || "Sem descrição"}</div>
                </div>
                <div className="rowMeta muted">#{c.id}</div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
