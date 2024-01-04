export interface IProject {
  id: string;
  name: string;
  description: string;
  workspaceId: string;
  apiTemplateId?: string;
  caseTemplateId?: string;
  issueTemplateId?: string;
  memberSize?: number;
}
