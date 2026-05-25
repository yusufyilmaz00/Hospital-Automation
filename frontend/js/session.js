//
// Utkan Başurgan
//
//--------------------------------------------------------------------------------------------------------------------------------

function getSession()
{
  const raw = localStorage.getItem(SESSION_KEY);
  if (!raw)
  {
    return null;
  }

  const session = JSON.parse(raw);
  if (!isTestMode() && session.source !== "api")
  {
    clearSession();
    return null;
  }

  return session;
}

function setSession(session)
{
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
}

function clearSession()
{
  localStorage.removeItem(SESSION_KEY);
}

function sessionLabel(session)
{
  if (!session)
  {
    return "";
  }
  if (session.kind === "patient")
  {
    return (session.name || session.email || "Bilinmiyor") + " (Hasta)";
  }
  const label = (session.roleLabel || "Personel") + " (" + session.role + ")";
  return session.email ? label + " [" + session.email + "]" : label;
}

function hasRole(session, roles)
{
  if (!session)
  {
    return false;
  }
  if (session.kind === "patient")
  {
    return roles.indexOf("patient") >= 0;
  }
  return roles.indexOf(session.role) >= 0;
}

//--------------------------------------------------------------------------------------------------------------------------------
