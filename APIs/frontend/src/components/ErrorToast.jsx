import { Toaster, toast } from "react-hot-toast";

export function fireErrors(errors = []) {
  const list = Array.isArray(errors) ? errors : [{ title: "Error", details: String(errors), code: 0 }];

  list.forEach(({ title, details, code }) =>
    toast.error(
      <div>
        <strong>{title} <small>({code})</small></strong>
        <br />
        <span style={{ fontSize: "0.8rem" }}>{details}</span>
      </div>,
      { duration: 4000 }
    )
  );
}

export default function ErrorToast() {
  return <Toaster position="top-center" reverseOrder={false} />;
}
