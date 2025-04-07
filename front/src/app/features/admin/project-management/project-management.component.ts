import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzModalService } from 'ng-zorro-antd/modal';
import { ProjectService } from '../../../services/project.service';
import { UserService } from '../../../services/user.service';

@Component({
  selector: 'app-project-management',
  templateUrl: './project-management.component.html',
  styleUrls: ['./project-management.component.css']
})
export class ProjectManagementComponent implements OnInit {
  projects: any[] = [];
  isLoading = false;
  isModalVisible = false;
  projectForm!: FormGroup;
  isEditMode = false;
  currentProjectId: number | null = null;
  selectedEmployees: number[] = [];

  projectStatuses = [
    { label: 'Planning', value: 'PLANNING' },
    { label: 'In Progress', value: 'IN_PROGRESS' },
    { label: 'Completed', value: 'COMPLETED' },
    { label: 'On Hold', value: 'ON_HOLD' },
    { label: 'Cancelled', value: 'CANCELLED' }
  ];

  hrManagers: any[] = [];
  clients: any[] = [];
  employees: any[] = [];

  constructor(
    private projectService: ProjectService,
    private userService: UserService,
    private fb: FormBuilder,
    private message: NzMessageService,
    private modal: NzModalService
  ) {}

  ngOnInit(): void {
    this.loadProjects();
    this.loadHRManagers();
    this.loadClients();
    this.loadEmployees();
    this.initForm();
  }

  loadProjects(): void {
    this.isLoading = true;
    this.projectService.getAllProjects().subscribe({
      next: (data) => {
        this.projects = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.message.error('Failed to load projects');
        this.isLoading = false;
        console.error('Error loading projects:', error);
      }
    });
  }

  loadHRManagers(): void {
    this.userService.getHRManagers().subscribe({
      next: (data) => {
        this.hrManagers = data;
      },
      error: (error) => {
        this.message.error('Failed to load HR managers');
        console.error('Error loading HR managers:', error);
      }
    });
  }

  loadClients(): void {
    this.userService.getAllClients().subscribe({
      next: (data) => {
        this.clients = data;
      },
      error: (error) => {
        this.message.error('Failed to load clients');
        console.error('Error loading clients:', error);
      }
    });
  }

  loadEmployees(): void {
    this.userService.getAllEmployees().subscribe({
      next: (data) => {
        this.employees = data;
      },
      error: (error) => {
        this.message.error('Failed to load employees');
        console.error('Error loading employees:', error);
      }
    });
  }

  initForm(): void {
    this.projectForm = this.fb.group({
      name: [null, [Validators.required]],
      description: [null],
      startDate: [new Date(), [Validators.required]],
      endDate: [null],
      status: ['PLANNING', [Validators.required]],
      budget: [0, [Validators.required, Validators.min(0)]],
      clientId: [null],
      managerId: [null, [Validators.required]],
      employeeIds: [[]],
      active: [true]
    });
  }

  showModal(project?: any): void {
    this.isEditMode = !!project;

    if (project) {
      this.currentProjectId = project.id;
      this.selectedEmployees = project.employees?.map((emp: any) => emp.id) || [];

      // Convert string dates to Date objects for the datepicker
      const startDate = project.startDate ? new Date(project.startDate) : null;
      const endDate = project.endDate ? new Date(project.endDate) : null;

      this.projectForm.patchValue({
        name: project.name,
        description: project.description,
        startDate: startDate,
        endDate: endDate,
        status: project.status,
        budget: project.budget,
        clientId: project.client?.id,
        managerId: project.manager?.id,
        employeeIds: this.selectedEmployees,
        active: project.active
      });
    } else {
      this.currentProjectId = null;
      this.selectedEmployees = [];
      this.projectForm.reset({
        startDate: new Date(),
        status: 'PLANNING',
        budget: 0,
        active: true,
        employeeIds: []
      });
    }

    this.isModalVisible = true;
  }

  submitForm(): void {
    if (this.projectForm.valid) {
      const projectData = this.projectForm.value;

      // Convert Date objects to ISO strings
      if (projectData.startDate) {
        projectData.startDate = new Date(projectData.startDate).toISOString();
      }
      if (projectData.endDate) {
        projectData.endDate = new Date(projectData.endDate).toISOString();
      }

      if (this.isEditMode && this.currentProjectId) {
        // Update project
        this.projectService.updateProject(this.currentProjectId, projectData).subscribe({
          next: () => {
            this.message.success('Project updated successfully');
            this.isModalVisible = false;
            this.loadProjects();
          },
          error: (error) => {
            this.message.error('Failed to update project');
            console.error('Error updating project:', error);
          }
        });
      } else {
        // Create project
        this.projectService.createProject(projectData).subscribe({
          next: () => {
            this.message.success('Project created successfully');
            this.isModalVisible = false;
            this.loadProjects();
          },
          error: (error) => {
            this.message.error('Failed to create project');
            console.error('Error creating project:', error);
          }
        });
      }
    } else {
      Object.values(this.projectForm.controls).forEach(control => {
        if (control.invalid) {
          control.markAsDirty();
          control.updateValueAndValidity({ onlySelf: true });
        }
      });
    }
  }

  cancelModal(): void {
    this.isModalVisible = false;
  }

  confirmDeactivate(projectId: number, active: boolean): void {
    this.modal.confirm({
      nzTitle: active ? 'Deactivate Project' : 'Activate Project',
      nzContent: active ? 'Are you sure you want to deactivate this project?' : 'Are you sure you want to activate this project?',
      nzOkText: 'Yes',
      nzOkType: 'primary',
      nzOkDanger: active,
      nzOnOk: () => this.toggleProjectActive(projectId),
      nzCancelText: 'No'
    });
  }

  toggleProjectActive(projectId: number): void {
    this.projectService.toggleProjectActive(projectId).subscribe({
      next: () => {
        this.message.success('Project status updated successfully');
        this.loadProjects();
      },
      error: (error) => {
        this.message.error('Failed to update project status');
        console.error('Error updating project status:', error);
      }
    });
  }

  deleteProject(projectId: number): void {
    this.modal.confirm({
      nzTitle: 'Delete Project',
      nzContent: 'Are you sure you want to delete this project? This action cannot be undone.',
      nzOkText: 'Yes',
      nzOkType: 'primary',
      nzOkDanger: true,
      nzOnOk: () => {
        this.projectService.deleteProject(projectId).subscribe({
          next: () => {
            this.message.success('Project deleted successfully');
            this.loadProjects();
          },
          error: (error) => {
            this.message.error('Failed to delete project');
            console.error('Error deleting project:', error);
          }
        });
      },
      nzCancelText: 'No'
    });
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PLANNING': return 'blue';
      case 'IN_PROGRESS': return 'processing';
      case 'COMPLETED': return 'success';
      case 'ON_HOLD': return 'warning';
      case 'CANCELLED': return 'error';
      default: return 'default';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'PLANNING': return 'Planning';
      case 'IN_PROGRESS': return 'In Progress';
      case 'COMPLETED': return 'Completed';
      case 'ON_HOLD': return 'On Hold';
      case 'CANCELLED': return 'Cancelled';
      default: return status;
    }
  }

  formatCurrency(value: number): string {
    return `$ ${value}`;
  }

  parseCurrency(value: string): string {
    return value.replace('$ ', '');
  }
}
