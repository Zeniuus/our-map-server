import { AxiosResponse } from 'axios';

// refs: https://stackoverflow.com/questions/16086162/handle-file-download-from-ajax-post
export function downloadAttachment(res: AxiosResponse) {
  if (res.status === 200) {
    const blob = new Blob([res.data]);
    const disposition = res.headers['content-disposition'];
    let filename = '';
    if (disposition && disposition.indexOf('attachment') !== -1) {
      const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
      const matches = filenameRegex.exec(disposition);
      if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
    }

    if ('msSaveBlob' in window.navigator) {
        // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
        (window.navigator as any).msSaveBlob(blob, filename);
    } else {
      const URL = window.URL || window.webkitURL;
      const downloadUrl = URL.createObjectURL(blob);

      if (filename) {
        // use HTML5 a[download] attribute to specify filename
        const a = document.createElement('a');
        // safari doesn't support this yet
        if (typeof a.download === 'undefined') {
          window.location.href = downloadUrl;
        } else {
          a.href = downloadUrl;
          a.download = filename;
          document.body.appendChild(a);
          a.click();
        }
      } else {
        window.location.href = downloadUrl;
      }
      setTimeout(function () { URL.revokeObjectURL(downloadUrl); }, 100); // cleanup
    }
  }
}
