import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { AccountService } from '../../../services/account.service';
import { ProjectService } from '../../../services/project.service';
import { Color, ScaleType } from '@swimlane/ngx-charts';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  // Stats
  totalUsers = 0;
  totalEmployees = 0;
  totalClients = 0;
  totalAccounts = 0;
  activeAccounts = 0;
  inactiveAccounts = 0;
  totalProjects = 0;
  activeProjects = 0;

  // Active vs. Inactive accounts chart
  accountStatusData: any[] = [];

  // User roles chart
  userRolesData: any[] = [];

  // Account types chart
  accountTypesData: any[] = [];

  // Project status chart
  projectStatusData: any[] = [];

  // Recent accounts
  recentAccounts: any[] = [];

  // Recent users
  recentUsers: any[] = [];

  // Recent projects
  recentProjects: any[] = [];

  // Charts options
  showLegend = true;
  showLabels = true;
  explodeSlices = false;
  doughnut = false;
  colorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1', '#13c2c2']
  };

  // Loading
  isLoading = true;

  constructor(
    private userService: UserService,
    private accountService: AccountService,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    this.isLoading = true;

    // Load total users and prepare chart data
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.totalUsers = data.length;
        this.totalEmployees = data.filter(user => user.role === 'EMPLOYEE').length;
        this.totalClients = data.filter(user => user.role === 'CLIENT').length;

        // Get the 5 most recently created users
        this.recentUsers = [...data]
          .sort((a, b) => new Date(b.createdDate).getTime() - new Date(a.createdDate).getTime())
          .slice(0, 5);

        // Prepare user roles chart data
        const adminCount = data.filter(user => user.role === 'ADMIN').length;
        const hrManagerCount = data.filter(user => user.role === 'HR_MANAGER').length;

        this.userRolesData = [
          { name: 'Administrators', value: adminCount },
          { name: 'HR Managers', value: hrManagerCount },
          { name: 'Employees', value: this.totalEmployees },
          { name: 'Clients', value: this.totalClients }
        ];
      },
      error: (error) => {
        console.error('Error loading users:', error);
        this.isLoading = false;
      }
    });

    // Load account data and prepare chart data
    this.accountService.getAllAccounts().subscribe({
      next: (data) => {
        this.totalAccounts = data.length;
        this.activeAccounts = data.filter(account => account.active).length;
        this.inactiveAccounts = data.filter(account => !account.active).length;

        // Get the 5 most recently created accounts
        this.recentAccounts = [...data]
          .sort((a, b) => new Date(b.createdDate).getTime() - new Date(a.createdDate).getTime())
          .slice(0, 5);

        // Prepare account status chart data
        this.accountStatusData = [
          { name: 'Active Accounts', value: this.activeAccounts },
          { name: 'Inactive Accounts', value: this.inactiveAccounts }
        ];

        // Prepare account types chart data
        const checkingAccounts = data.filter(account => account.accountType === 'CHECKING').length;
        const savingsAccounts = data.filter(account => account.accountType === 'SAVINGS').length;
        const investmentAccounts = data.filter(account => account.accountType === 'INVESTMENT').length;
        const loanAccounts = data.filter(account => account.accountType === 'LOAN').length;

        this.accountTypesData = [
          { name: 'Checking', value: checkingAccounts },
          { name: 'Savings', value: savingsAccounts },
          { name: 'Investment', value: investmentAccounts },
          { name: 'Loan', value: loanAccounts }
        ];
      },
      error: (error) => {
        console.error('Error loading accounts:', error);
      }
    });

    // Load project data and prepare chart data
    this.projectService.getAllProjects().subscribe({
      next: (data) => {
        this.totalProjects = data.length;
        this.activeProjects = data.filter(project => project.active).length;

        // Get the 5 most recently created projects
        this.recentProjects = [...data]
          .sort((a, b) => new Date(b.startDate).getTime() - new Date(a.startDate).getTime())
          .slice(0, 5);

        // Prepare project status chart data
        const planningProjects = data.filter(project => project.status === 'PLANNING').length;
        const inProgressProjects = data.filter(project => project.status === 'IN_PROGRESS').length;
        const completedProjects = data.filter(project => project.status === 'COMPLETED').length;
        const onHoldProjects = data.filter(project => project.status === 'ON_HOLD').length;
        const cancelledProjects = data.filter(project => project.status === 'CANCELLED').length;

        this.projectStatusData = [
          { name: 'Planning', value: planningProjects },
          { name: 'In Progress', value: inProgressProjects },
          { name: 'Completed', value: completedProjects },
          { name: 'On Hold', value: onHoldProjects },
          { name: 'Cancelled', value: cancelledProjects }
        ];

        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading projects:', error);
        this.isLoading = false;
      }
    });
  }

  onSelect(event: any): void {
    console.log('Item clicked', event);
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

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  }
}
